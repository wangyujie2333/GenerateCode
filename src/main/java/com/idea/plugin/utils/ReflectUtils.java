package com.idea.plugin.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectUtils {

    public static <T> T getObjectInstance(Class<T> tClass, List<String> designStrlist) throws Exception {
        Constructor<?>[] declaredConstructors = tClass.getDeclaredConstructors();
        T tobject = (T) declaredConstructors[0].newInstance();
        Map<String, Object> classFieldInfoMap = new HashMap<>();
        getClassFeildInfo(tClass, classFieldInfoMap);
        String fieldInfo = "";
        String fieldName = null;
        Boolean newSubField = false;
        String oldSubFieldName = null;
        Map<String, Object> fieldValueMap = new HashMap<>();
        Map<String, Set<String>> subFieldNameMap = new HashMap<>();
        Map<String, Class<?>> subFieldClassMap = new HashMap<>();
        Map<String, List<Object>> subFieldValueMap = new HashMap<>();
        Class<?> subFieldClass = null;
        List<String> subDesignStrlist = new ArrayList<>();
        for (String property : designStrlist) {
//            if (StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(fieldInfo)) {
//                String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//                Method method = tClass.getDeclaredMethod(methodName);
//                method.invoke(tobject, fieldInfo);
//            }
            property = property.trim();
            property = property.replaceFirst("：", ":");
            if (StringUtils.isEmpty(property) || property.startsWith("--")) {
                continue;
            }
            int index = property.indexOf(":");
            if (String.class.getName().equals(classFieldInfoMap.get(fieldName))) {
                fieldValueMap.put(fieldName, fieldInfo);
            }
            if (index > 0) {
                fieldInfo = property.substring(index + 1);
                fieldName = property.substring(0, index).trim();
                if (fieldName.contains(".")) {
                    String subFieldName = fieldName.substring(property.indexOf(".") + 1);
                    fieldName = fieldName.substring(0, property.indexOf("."));
                    if (!subFieldClassMap.containsKey(fieldName)) {
                        subFieldClassMap.put(fieldName, (Class<?>) classFieldInfoMap.get(fieldName));
                    }
                    if (!subFieldNameMap.containsKey(fieldName)) {
                        subFieldNameMap.put(fieldName, new HashSet<>());
                    } else {
                        if (subFieldNameMap.get(fieldName).contains(subFieldName)) {
                            subFieldNameMap.put(fieldName, new HashSet<>());
                            subDesignStrlist = new ArrayList<>();
                            newSubField = true;
                        }
                    }
                    if (newSubField && subFieldClass != null) {
                        Object objectInstance = getObjectInstance(subFieldClass, subDesignStrlist);
                        if (!subFieldValueMap.containsKey(oldSubFieldName)) {
                            subFieldValueMap.put(oldSubFieldName, new ArrayList<>());
                        }
                        subFieldValueMap.get(oldSubFieldName).add(objectInstance);
                        oldSubFieldName = subFieldName;
                        subDesignStrlist = new ArrayList<>();
                    }
                    subFieldNameMap.get(fieldName).add(subFieldName);
                    subFieldClass = (Class<?>) classFieldInfoMap.get(fieldName);
                    subDesignStrlist.add(property.substring(property.indexOf(".") + 1));
                }
            } else {
                fieldInfo = fieldInfo + "\n" + property.substring(index + 1);
            }

        }
        if (classFieldInfoMap.containsKey(fieldName)) {
            if (StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(fieldInfo)) {
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method = tClass.getDeclaredMethod(methodName);
                method.invoke(tobject, fieldInfo);
            }
        }
        return tobject;
    }

    public static <T> void getClassFeildInfo(Class<T> tClass, Map<String, Object> classFieldInfoMap) {
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            Class<?> type = field.getType();
            if (String.class.getName().equals(type.getName())) {
                classFieldInfoMap.put(name, String.class);
            } else if (List.class.getName().equals(type.getName())) {
                ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                try {
                    Map<String, Object> paramFieldInfoMap = new HashMap<>();
                    Class<?> paramClass = Class.forName(paramType.getActualTypeArguments()[0].getTypeName());
                    getClassFeildInfo(paramClass, paramFieldInfoMap);
                    classFieldInfoMap.put(name, paramClass);
                    paramFieldInfoMap.forEach((key, value) -> {
                        classFieldInfoMap.put(name + "." + key, value);
                    });
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> getClassFeildNames(Class<?> tClass) {
        List<String> fieldNames = Arrays.stream(tClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        Class<?> superclass = tClass.getSuperclass();
        while (!superclass.equals(Object.class)) {
            fieldNames.addAll(getClassFeildNames(superclass));
            superclass = superclass.getSuperclass();
        }
        return fieldNames;
    }

}
