package com.idea.plugin.utils;

import com.google.gson.*;
import com.idea.plugin.document.support.JavaDocConfig;
import com.idea.plugin.document.support.JavaTypeEnum;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.text.JsonFormatService;
import com.idea.plugin.translator.TranslatorFactroy;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class JsonUtil {
    private static final Pattern pattern = Pattern.compile("^([\\w.]+:)");
    private static final Pattern fieldPattern = Pattern.compile("(\\s*)(private)(\\s+)([A-Za-z<>, ]+)(\\s+)(\\w+)");

    public static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    public static final Gson gson = new GsonBuilder().create();

    public static final JsonFormatService jsonFormatService = new JsonFormatService();


    private JsonUtil() {
    }

    public static <T> String prettyJson(T json) {
        if (json == null || StringUtils.isEmpty(json.toString())) {
            return null;
        }
        if (json instanceof String) {
            return prettyJson(fromJson(json.toString()));
        }
        try {
            return prettyGson.toJson(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> String toJson(T json) {
        if (json == null || StringUtils.isEmpty(json.toString())) {
            return null;
        }
        if (json instanceof String) {
            return toJson(fromJson(json.toString(), Map.class));
        }
        try {
            return gson.toJson(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String jsonToObject(String json) {
        return jsonToObject(json, "JsonRootBean", false, false);
    }

    public static String jsonToObject(String json, boolean issetget, boolean isdoc) {
        return jsonToObject(json, "JsonRootBean", issetget, isdoc);
    }

    public static String jsonToObject(String json, String beanName) {
        return jsonToObject(json, beanName, false, false);
    }

    public static String jsonToObject(String json, String beanName, boolean issetget, boolean isdoc) {
        if (StringUtils.isEmpty(json)) {
            return "";
        }
        String clazzNameStr = "public%s class %s {\n" +
                "%s\n" +
                "}\n";
        String fieldNameStr = "    private %s %s;\n";
        JsonElement jsonRoot = JsonUtil.fromJson(json, JsonElement.class);
        List<String> fieldName = new ArrayList<>();
        List<String> fieldMethod = new ArrayList();
        if (jsonRoot == null) {
            return "";
        }
        JsonObject jsonObject = null;
        if (jsonRoot instanceof JsonPrimitive) {
            if (isdoc) {
                fieldName.add(JavaDocConfig.getFieldComment(beanName) + "\n");
            }
            String type = getPrimitiveFieldStr(fieldNameStr, fieldName, beanName, (JsonPrimitive) jsonRoot);
            if (issetget) {
                fieldMethod.add("    " + JavaDocConfig.getSetMethodStr(type, beanName));
                fieldMethod.add("    " + JavaDocConfig.getGetMethodStr(type, beanName));
            }
            return fieldName.toString();
        } else if (jsonRoot.isJsonArray()) {
            jsonObject = (JsonObject) jsonRoot.getAsJsonArray().get(0);
        } else if (jsonRoot.isJsonObject()) {
            jsonObject = (JsonObject) jsonRoot;
        }
        if (jsonObject == null) {
            return "";
        }
        List<String> subClassList = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            JsonElement jsonElement = jsonObject.get(key);
            if (isdoc) {
                fieldName.add(JavaDocConfig.getFieldComment(key) + "\n");
            }
            String type = "";
            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                String clazzName = key.substring(0, 1).toUpperCase() + key.substring(1);
                JsonElement listJsonElement = JsonUtil.fromJson(jsonElement.getAsJsonArray().get(0).toString(), JsonElement.class);
                if (listJsonElement instanceof JsonPrimitive) {
                    type = getPrimitiveFieldStr(fieldNameStr, fieldName, key, (JsonPrimitive) listJsonElement);
                } else {
                    type = "List<" + clazzName + ">";
                    fieldName.add(String.format(fieldNameStr, "List<" + clazzName + ">", key));
                    subClassList.add(jsonToObject(jsonElement.getAsJsonArray().get(0).toString(), clazzName, issetget, isdoc));
                }
            } else if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().size() > 0) {
                String clazzName = key.substring(0, 1).toUpperCase() + key.substring(1);
                type = clazzName;
                fieldName.add(String.format(fieldNameStr, clazzName, key));
                subClassList.add(jsonToObject(jsonElement.getAsJsonObject().toString(), clazzName, issetget, isdoc));
            } else if (jsonElement.isJsonPrimitive()) {
                type = getPrimitiveFieldStr(fieldNameStr, fieldName, key, (JsonPrimitive) jsonElement);
            } else {
                type = "String";
                fieldName.add(String.format(fieldNameStr, type, key));
            }
            if (issetget) {
                fieldMethod.add("    " + JavaDocConfig.getSetMethodStr(type, key));
                fieldMethod.add("    " + JavaDocConfig.getGetMethodStr(type, key));
            }
        }
        String staticStr = "";
        if (!beanName.equals("JsonRootBean")) {
            staticStr = " static";
        }
        subClassList = subClassList.stream().flatMap(s -> Arrays.stream(s.split("\n"))).collect(Collectors.toList());
        String subClassStr = subClassList.stream().map(s -> "    " + s).collect(Collectors.joining("\n"));
        String classStr = "";
        if (isdoc) {
            classStr = JavaDocConfig.getClazzComment(beanName);
        }
        String constructor = "";
        if (issetget) {
            constructor = "    public " + beanName + "() {\n    }\n\n";
        }
        if (StringUtils.isEmpty(subClassStr)) {
            subClassStr = "\n";
        } else {
            subClassStr = "\n\n" + subClassStr;
        }
        String classBody = String.join("", fieldName) + "\n" + constructor + String.join("\n\n", fieldMethod);
        String clazzStrformat = String.format(clazzNameStr, staticStr, beanName, classBody + subClassStr);
        return classStr + clazzStrformat;
    }


    private static String getPrimitiveFieldStr(String fieldNameStr, List<String> fieldName, String key, JsonPrimitive jsonElement) {
        String type = getPrimitiveType(jsonElement);
        fieldName.add(String.format(fieldNameStr, type, key));
        return type;
    }

    public static String getPrimitiveType(JsonPrimitive jsonElement) {
        String type = "String";
        if (jsonElement.isBoolean()) {
            type = "Boolean";
        } else if (jsonElement.isNumber()) {
            type = "BigDecimal";
        }
        return type;
    }

    public static Object getPrimitiveValue(JsonPrimitive jsonElement) {
        Object value;
        if (jsonElement.isBoolean()) {
            value = jsonElement.getAsBoolean();
        } else if (jsonElement.isNumber()) {
            value = jsonElement.getAsBigDecimal();
        } else if (jsonElement.isJsonNull()) {
            value = null;
        } else {
            value = jsonElement.getAsString();
        }
        return value;
    }

    private static String getSubFieldInfo(Map<String, Object> subMap) {
        StringBuilder sb = new StringBuilder();
        if (MapUtils.isNotEmpty(subMap)) {
            subMap.forEach((key, value) -> {
                sb.append("\n    //======" + key + " is start======\n");
                sb.append(jsonToObject(toJson(value), key + "Bean"));
                sb.append("    //======" + key + " is end======\n");
            });
        }
        return sb.toString();
    }

    public static String objectToJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        String[] fieldArr = json.split("\n");
        List<String> fieldList = Arrays.stream(fieldArr).collect(Collectors.toList());
        List<String> fieldNames = getObjectFieldNames(fieldList);
        return getJsonStrByFileStr(fieldNames);
    }

    public static String propertyToJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        String[] fieldArr = json.split("\n");
        List<String> fieldList = Arrays.stream(fieldArr).collect(Collectors.toList());
        return getJsonStrByFileStr(fieldList);
    }

    public static String jsonToProperty(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        List<String> result = getPropertyStr(fromJson(json, Map.class));
        return String.join("\n", result);
    }

    private static List<String> getPropertyStr(Map<String, Object> map) {
        List<String> result = new ArrayList<>();
        map.forEach((key, value) -> {
            if (value instanceof List) {
                ((List<?>) value).forEach(str -> {
                    result.add(key + ":" + str);
                });
            } else if (value instanceof Map) {
                List<String> subResult = getPropertyStr((Map<String, Object>) value);
                subResult.forEach(str -> {
                    result.add(key + "." + str);
                });
            } else {
                result.add(key + ":" + value);
            }
        });
        return result;
    }

    private static List<String> getObjectFieldNames(List<String> fieldList) {
        List<String> fieldNames = new ArrayList<>();
        List<String> filterFieldList = new ArrayList<>();
        for (int i = 0; i < fieldList.size(); i++) {
            String field = fieldList.get(i);
            Matcher matcher = fieldPattern.matcher(field);
            if (matcher.find()) {
                filterFieldList.add(field);
            }
        }
        filterFieldList.forEach(field -> {
            Matcher matcher = fieldPattern.matcher(field);
            if (matcher.find()) {
                String fieldType = matcher.group(4);
                String fieldName = matcher.group(6);
                if (!fieldType.contains("static") && !fieldType.contains("static")) {
                    if (fieldType.startsWith(JavaTypeEnum.LIST_TYPE.getName())) {
                        fieldNames.add(fieldName + ":" + TranslatorFactroy.translate(fieldName));
                        fieldNames.add(fieldName + ": ");
                    } else if (fieldType.startsWith(JavaTypeEnum.MAP_TYPE.getName())) {
                        fieldNames.add(fieldName + "." + fieldName + ":" + TranslatorFactroy.translate(fieldName));
                    } else {
                        if (Arrays.stream(JavaTypeEnum.values()).anyMatch(javaTypeEnum -> fieldType.equals(javaTypeEnum.getName()))) {
                            fieldNames.add(fieldName + ":" + TranslatorFactroy.translate(fieldName));
                        } else {
                            ActionContext context = ThreadLocalUtils.get(ActionContext.class, "ActionContext");
                            if (context != null) {
                                PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(fieldType, GlobalSearchScope.projectScope(context.getProject()));
                                if (psiClasses.length > 0) {
                                    AtomicInteger downcount = new AtomicInteger(1);
                                    Map<String, Object> allFieldNames = jsonFormatService.getAllFieldNames(context, psiClasses[0], downcount);
                                    String objectFieldStr = jsonToObject(toJson(allFieldNames), psiClasses[0].getName());
                                    if (objectFieldStr != null) {
                                        List<String> subFieldNames = getObjectFieldNames(Arrays.stream(objectFieldStr.split("\n")).collect(Collectors.toList()));
                                        subFieldNames = subFieldNames.stream().map(s -> fieldName + "." + s).collect(Collectors.toList());
                                        fieldNames.addAll(subFieldNames);
                                    } else {
                                        fieldNames.add(fieldName + ":" + TranslatorFactroy.translate(fieldName));
                                    }
                                } else {
                                    fieldNames.add(fieldName + ":" + TranslatorFactroy.translate(fieldName));
                                }
                            } else {
                                fieldNames.add(fieldName + ":" + TranslatorFactroy.translate(fieldName));
                            }
                        }
                    }
                }
            }
        });
        return fieldNames;
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return gson.fromJson(json, tClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object fromJson(String json) {
        Object object = null;
        try {
            object = JsonUtil.fromJson(json, JsonObject.class);
        } catch (Exception e) {
        }
        try {
            object = JsonUtil.fromJson(json, JsonArray.class);
        } catch (Exception e) {
        }
        return object;
    }

    public static Boolean isJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            return toJson(json) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean isObject(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            return objectToJson(json) != null;
        } catch (Exception e) {
            return false;
        }
    }


    public static Boolean isProperty(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            return propertyToJson(json) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getJsonStrByFileStr(List<String> designStrlist) {
        Map<String, Object> jsonMapByFileStr = getJsonMapByFileStr(designStrlist);
        if (MapUtils.isNotEmpty(jsonMapByFileStr)) {
            return toJson(jsonMapByFileStr);
        }
        return null;
    }

    public static Map<String, Object> getJsonMapByFileStr(List<String> designStrlist) {
        designStrlist.add("");
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        String fieldInfo = "";
        String fieldName = null;
        String subFieldName;
        boolean subField = false;
        AtomicBoolean isnewsub = new AtomicBoolean(false);
        int fieldEnd = 0;
        int subFieldStart = 0;
        int subFieldEnd = 0;
        boolean newSubFieldName = false;
        Map<String, Set<String>> subFieldNameMap = new LinkedHashMap<>();
        List<Map<String, List<String>>> subDesignStrMapList = new ArrayList<>();
        Map<String, List<String>> subDesignStrMap = new LinkedHashMap<>();
        List<String> subDesignStrList = new ArrayList<>();
        String subDesignStr = "";
        for (int i = 0; i <= designStrlist.size(); i++) {
            if (subField) {
                if (!newSubFieldName && !subDesignStrList.isEmpty()) {
                    subDesignStrList.remove(subDesignStrList.size() - 1);
                }
                subDesignStrList.add(subDesignStr);
                if (subFieldEnd + 1 == i) {
                    subDesignStrList = subDesignStrList.stream().flatMap(s -> Arrays.stream(s.split("\n"))).collect(Collectors.toList());
                    subFieldName = fieldName;
                    if (subDesignStrMap.containsKey(subFieldName)) {
                        subDesignStrMap = new LinkedHashMap<>();
                    }
                    subDesignStrMap.put(subFieldName, subDesignStrList);
                    if (!subDesignStrMapList.contains(subDesignStrMap)) {
                        subDesignStrMapList.add(subDesignStrMap);
                    }
                    subDesignStr = "";
                    subDesignStrList = new ArrayList<>();
                }
            } else if (StringUtils.isNotEmpty(fieldName)) {
                if (fieldEnd + 1 == i) {
                    if (propertyMap.containsKey(fieldName) && StringUtils.isNotEmpty(propertyMap.get(fieldName).toString())) {
                        List<Object> subPropertyList;
                        if (propertyMap.get(fieldName) instanceof List) {
                            subPropertyList = (List<Object>) propertyMap.get(fieldName);
                        } else {
                            subPropertyList = new ArrayList<>();
                            subPropertyList.add(propertyMap.get(fieldName));
                        }
                        subPropertyList.add(fieldInfo);
                        subPropertyList = subPropertyList.stream().filter(o -> o != null && StringUtils.isNotBlank(o.toString())).collect(Collectors.toList());
                        propertyMap.put(fieldName, subPropertyList);
                    } else {
                        propertyMap.put(fieldName, fieldInfo);
                    }
                }
            }
            if (i == designStrlist.size()) {
                continue;
            }
            String property = designStrlist.get(i);
            if (StringUtils.isEmpty(property) || property.startsWith("---")) {
                subDesignStr = subDesignStr + "  \n";
                fieldInfo = fieldInfo + "  \n";
                newSubFieldName = false;
                continue;
            }
            Matcher matcher = pattern.matcher(property);
            if (matcher.find()) {
                int end = i;
                for (int j = i + 1; j < designStrlist.size(); j++) {
                    String propertyNext = designStrlist.get(j).trim();
                    fieldEnd = end;
                    matcher = pattern.matcher(propertyNext);
                    if (matcher.find()) {
                        break;
                    } else {
                        if (StringUtils.isNotEmpty(propertyNext)) {
                            end = j;
                        }
                    }
                }
                subField = false;
                subFieldStart = getSubFieldStart(designStrlist, isnewsub, subFieldStart, subFieldEnd, subFieldNameMap, i);
                if (isnewsub.get()) {
                    subFieldEnd = getSubFieldEnd(designStrlist, subFieldStart, subFieldEnd, subFieldNameMap, i);
                }
                int index = property.indexOf(":");
                fieldInfo = property.substring(index + 1);
                fieldName = property.substring(0, index).trim();
                if (fieldName.contains(".")) {
                    subField = true;
                    newSubFieldName = true;
                    fieldName = fieldName.substring(0, property.indexOf("."));
                    subDesignStr = property.substring(property.indexOf(".") + 1);
                }
            } else {
                if (fieldInfo.trim().endsWith(":")) {
                    fieldInfo = fieldInfo + property;
                } else {
                    fieldInfo = fieldInfo + "  \n" + property;
                }
                if (subDesignStr.trim().endsWith(":")) {
                    subDesignStr = subDesignStr + property;
                } else {
                    subDesignStr = subDesignStr + "  \n" + property;
                }
                newSubFieldName = false;
            }
        }
        subDesignStrMapList.forEach(map -> {
            map.forEach((key, value) -> {
                Map<String, Object> subPropertyMap = getJsonMapByFileStr(value);
                if (propertyMap.containsKey(key)) {
                    List<Object> subPropertyMapList;
                    if (propertyMap.get(key) instanceof List) {
                        subPropertyMapList = (List<Object>) propertyMap.get(key);
                    } else {
                        subPropertyMapList = new ArrayList<>();
                        subPropertyMapList.add(propertyMap.get(key));
                    }
                    subPropertyMapList.add(subPropertyMap);
                    propertyMap.put(key, subPropertyMapList);
                } else {
                    propertyMap.put(key, subPropertyMap);
                }
            });
        });
        return propertyMap;
    }

    private static int getSubFieldStart(List<String> designStrlist, AtomicBoolean isnewsub, int subFieldStart,
                                        int subFieldEnd, Map<String, Set<String>> subFieldNameMap, int i) {
        for (int j = i; j < designStrlist.size(); j++) {
            String propertyNext = designStrlist.get(j).trim();
            Matcher matcher = pattern.matcher(propertyNext);
            if (matcher.find()) {
                int index = propertyNext.indexOf(":");
                String fieldName = propertyNext.substring(0, index).trim();
                if (fieldName.contains(".")) {
                    if (subFieldEnd >= i && i > 0) {
                        isnewsub.set(false);
                        return subFieldStart;
                    }
                    String subFieldNameNext = fieldName.substring(fieldName.indexOf(".") + 1);
                    String fieldNameNext = fieldName.substring(0, fieldName.indexOf(".")).trim();
                    if (subFieldNameMap.isEmpty() || !subFieldNameMap.containsKey(fieldNameNext)) {
                        isnewsub.set(true);
                        subFieldStart = j;
                        subFieldNameMap.put(fieldNameNext, new HashSet<>());
                        break;
                    } else if (subFieldNameMap.get(fieldNameNext).contains(subFieldNameNext)) {
                        isnewsub.set(true);
                        subFieldStart = j;
                        subFieldNameMap.put(fieldNameNext, new HashSet<>());
                        break;
                    }
                }
            }
        }
        return subFieldStart;
    }

    private static int getSubFieldEnd(List<String> designStrlist, int subFieldStart, int subFieldEnd, Map<
            String, Set<String>> subFieldNameMap, int i) {
        if (subFieldEnd > i || subFieldEnd > subFieldStart) {
            return subFieldEnd;
        }
        boolean issub = false;
        String preSubName = null;
        int end = subFieldStart;
        for (int j = subFieldStart; j < designStrlist.size(); j++) {
            subFieldEnd = end;
            String propertyNext = designStrlist.get(j).trim();
            Matcher matcher = pattern.matcher(propertyNext);
            if (matcher.find()) {
                int index = propertyNext.indexOf(":");
                String fieldName = propertyNext.substring(0, index).trim();
                if (fieldName.contains(".")) {
                    issub = true;
                    String subFieldNameNext = fieldName.substring(fieldName.indexOf(".") + 1);
                    String fieldNameNext = fieldName.substring(0, fieldName.indexOf(".")).trim();
                    if (!subFieldNameNext.contains(".")) {
                        if (!subFieldNameMap.containsKey(fieldNameNext)) {
                            break;
                        } else if (subFieldNameMap.get(fieldNameNext).contains(subFieldNameNext)
                                && !subFieldNameNext.equals(preSubName)) {
                            break;
                        }
                    }
                    if (StringUtils.isNotEmpty(propertyNext)) {
                        end = j;
                    }
                    preSubName = subFieldNameNext;
                    subFieldNameMap.get(fieldNameNext).add(subFieldNameNext);
                } else if (issub) {
                    if (StringUtils.isNotEmpty(propertyNext)) {
                        end = j;
                    }
                    break;
                }
            } else if (issub) {
                if (StringUtils.isNotEmpty(propertyNext)) {
                    end = j;
                }
            }
        }
        return subFieldEnd;
    }


}
