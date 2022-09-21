package com.idea.plugin.document.support;

import com.idea.plugin.orm.support.enums.ClazzTypeEnum;
import com.intellij.psi.PsiClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ClazzInfoVO {

    public static final Pattern paramPattern = Pattern.compile("<([\\w, <>.]+)>");

    public PsiClass psiClass;
    public String packageName;
    public String comment;
    public ClazzTypeEnum clazzType;
    public String clazzName;
    public String implClazz;
    public String resourceClazz;
    public ClazzInfoDOVO clazzInfoDOVO;

    public List<String> importList = new ArrayList<>();
    public List<FieldInfoVO> fieldinfos = new ArrayList<>();
    public List<MethodInfoVO> methodInfos = new ArrayList<>();

    public String getSimpleName(String clazzName) {
        if (clazzName == null) {
            return "";
        }
        if (clazzName.charAt(0) < 'A') {
            return clazzName.substring(1, 2).toLowerCase() + clazzName.substring(2);
        }
        return clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);
    }

    public String getClazzSimpleName(String clazzName) {
        if (StringUtils.isEmpty(clazzName) || JavaTypeEnum.VOID_TYPE.getBasicName().equals(clazzName)) {
            return "";
        }
        JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(clazzName);
        if (JavaTypeEnum.LIST_TYPE.equals(javaTypeEnum)) {
            Matcher matcher = paramPattern.matcher(clazzName);
            String subClazzName = null;
            if (matcher.find()) {
                subClazzName = matcher.group(1);
            }
            return getClazzSimpleName(subClazzName) + "List";
        } else if (JavaTypeEnum.MAP_TYPE.equals(javaTypeEnum)) {
            Matcher matcher = paramPattern.matcher(clazzName);
            String subClazzName = null;
            if (matcher.find()) {
                subClazzName = matcher.group(1).split(",")[1].trim();
            }
            return getClazzSimpleName(subClazzName) + "Map";
        } else {
            if (javaTypeEnum != null) {
                return getSimpleName(javaTypeEnum.getName());
            }
            return getSimpleName(clazzName.substring(clazzName.lastIndexOf(".") + 1));
        }
    }

    public String getClazzSimpleType(String clazzName) {
        if (clazzName == null || StringUtils.isEmpty(clazzName.trim())) {
            return "";
        }
        JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(clazzName);
        if (JavaTypeEnum.VOID_TYPE.equals(javaTypeEnum)) {
            return JavaTypeEnum.VOID_TYPE.getBasicName();
        }
        if (JavaTypeEnum.LIST_TYPE.equals(javaTypeEnum)) {
            Matcher matcher = paramPattern.matcher(clazzName);
            String subClazzName = null;
            if (matcher.find()) {
                subClazzName = matcher.group(1);
            }
            return "List<" + getClazzSimpleType(subClazzName) + ">";
        } else if (JavaTypeEnum.MAP_TYPE.equals(javaTypeEnum)) {
            Matcher matcher = paramPattern.matcher(clazzName);
            String subClazzName0 = null;
            String subClazzName1 = null;
            if (matcher.find()) {
                String[] split = matcher.group(1).split(",");
                subClazzName0 = split[0].trim();
                subClazzName1 = split[1].trim();
            }
            return "Map<" + getClazzSimpleType(subClazzName0) + ", " + getClazzSimpleType(subClazzName1) + ">";
        } else {
            if (javaTypeEnum != null) {
                return javaTypeEnum.getName();
            }
            return clazzName.substring(clazzName.lastIndexOf(".") + 1);
        }
    }

    public void addImport(String improtStr) {
        if (this.importList == null) {
            this.importList = new ArrayList<>();
        }
        if (!this.importList.contains(improtStr) && improtStr.contains(".")) {
            this.importList.add(improtStr);
        }
    }

    public void addClazzImports() {
        if (CollectionUtils.isNotEmpty(this.getMethodInfos())) {
            for (MethodInfoVO methodInfo : this.getMethodInfos()) {
                if (CollectionUtils.isNotEmpty(methodInfo.getMethodParameter().values())) {
                    for (String value : methodInfo.getMethodParameter().values()) {
                        addClazzImports(value);
                    }
                }
                if (CollectionUtils.isNotEmpty(methodInfo.getMethodThrowsList())) {
                    for (String value : methodInfo.getMethodThrowsList()) {
                        addClazzImports(value);
                    }
                }
                addClazzImports(methodInfo.getMethodReturn());
            }
        }
    }

    public void addClazzImports(String clazzName) {
        if (StringUtils.isEmpty(clazzName)) {
            return;
        }
        JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(clazzName);
        if (javaTypeEnum != null && javaTypeEnum.isImport()) {
            addImport(javaTypeEnum.getCalzz().getName());
        }
        if (JavaTypeEnum.LIST_TYPE.equals(javaTypeEnum)) {
            Matcher matcher = paramPattern.matcher(clazzName);
            String subClazzName = null;
            if (matcher.find()) {
                subClazzName = matcher.group(1);
                addImport(subClazzName);
            }
            addClazzImports(subClazzName);
        } else if (JavaTypeEnum.MAP_TYPE.equals(javaTypeEnum)) {
            Matcher matcher = paramPattern.matcher(clazzName);
            String subClazzName = null;
            if (matcher.find()) {
                subClazzName = matcher.group(1).split(",")[1].trim();
                addImport(subClazzName);
            }
            addClazzImports(subClazzName);
        } else {
            addImport(clazzName);
        }
    }

    public String getMethodParameter(Map<String, String> methodParameter) {
        if (MapUtils.isEmpty(methodParameter)) {
            return "";
        }
        return methodParameter.entrySet().stream().map(entry -> getClazzSimpleType(entry.getValue()) + " " + entry.getKey()).collect(Collectors.joining(", "));
    }

    public String getMethodParam(Map<String, String> methodParameter) {
        if (MapUtils.isEmpty(methodParameter)) {
            return "";
        }
        return methodParameter.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.joining(", "));

    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public String getPackageName() {
        return this.packageName.replaceAll("/", ".");
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ClazzTypeEnum getClazzType() {
        return clazzType;
    }

    public void setClazzType(ClazzTypeEnum clazzType) {
        this.clazzType = clazzType;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public ClazzInfoDOVO getClazzInfoDOVO() {
        return clazzInfoDOVO;
    }

    public void setClazzInfoDOVO(ClazzInfoDOVO clazzInfoDOVO) {
        this.clazzInfoDOVO = clazzInfoDOVO;
    }

    public String getImplClazz() {
        return implClazz;
    }

    public void setImplClazz(String implClazz) {
        this.implClazz = implClazz;
    }

    public String getResourceClazz() {
        return resourceClazz;
    }

    public void setResourceClazz(String resourceClazz) {
        this.resourceClazz = resourceClazz;
    }


    public List<String> getImportList() {
        return importList;
    }

    public void setImportList(List<String> importList) {
        this.importList = importList;
    }

    public List<FieldInfoVO> getFieldinfos() {
        return fieldinfos;
    }

    public void setFieldinfos(List<FieldInfoVO> fieldinfos) {
        this.fieldinfos = fieldinfos;
    }

    public List<MethodInfoVO> getMethodInfos() {
        return methodInfos;
    }

    public void setMethodInfos(List<MethodInfoVO> methodInfos) {
        this.methodInfos = methodInfos;
    }

    public void addMethodInfos(MethodInfoVO methodInfo) {
        if (this.methodInfos == null) {
            this.methodInfos = new ArrayList<>();
        }
        this.methodInfos.add(methodInfo);
    }

}
