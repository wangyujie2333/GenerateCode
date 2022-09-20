package com.idea.plugin.document.support;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.idea.plugin.utils.StringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaDocConfig {
    public static String clazzComment = "/**\n" +
            " * %s\n" +
            " *\n" +
            " * @author %s\n" +
            " * @date %s\n" +
            " */\n";
    public static String methodComment = "     /**\n" +
            "     * %s\n" +
            "     *\n" +
            "%s" +
            "     */\n";
    public static String fieldComment = "    /**\n" +
            "     * %s\n" +
            "     */";
    public static String localComment = "%s// %s\n";

    public static String constructorMethodStr = "public %s(%s) {\n" +
            "%s\n" +
            "    }";
    public static String setMethodStr = "public void %s(%s %s) {\n" +
            "        this.%s = %s;\n" +
            "    }";
    public static String getMethodStr = "public %s %s() {\n" +
            "        return %s;\n" +
            "    }";

    public static String getConstructorMethodStr(String clazzName, PsiField[] psiFields) {
        if (psiFields.length == 0) {
            return String.format(constructorMethodStr, clazzName, "", "");
        }
        ClazzInfoVO clazzInfoVO = new ClazzInfoVO();
        String params = Arrays.stream(psiFields).map(psiField ->
                clazzInfoVO.getClazzSimpleType(psiField.getType().getCanonicalText()) + " " + psiField.getName()).collect(Collectors.joining(", "));
        String body = Arrays.stream(psiFields).map(psiField ->
                "this" + psiField.getName() + " = " + psiField.getName()).collect(Collectors.joining("\n"));
        return String.format(constructorMethodStr, clazzName, params, body);
    }

    public static String getSetMethodStr(String type, String fieldName) {
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String setName = "set" + methodName;
        return String.format(setMethodStr, setName, type, fieldName, fieldName, fieldName);
    }

    public static String getGetMethodStr(String type, String fieldName) {
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String getName = "get" + methodName;
        return String.format(getMethodStr, type, getName, fieldName);
    }

    public static String getClazzComment(String clazzName) {
        if (clazzName == null) {
            return StringUtils.EMPTY;
        }
        String translate = TranslatorFactroy.translate(clazzName);
        String author = ToolSettings.getSettingConfig().getAuthor();
        String now = DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD_HH_MM_SS);
        return String.format(clazzComment, translate, author, now);
    }

    public static String getMethodComment(MethodInfoVO methodInfoVO) {
        if (methodInfoVO == null) {
            return StringUtils.EMPTY;
        }
        String translate = TranslatorFactroy.translate(methodInfoVO.getMethodName());
        StringBuilder comment = new StringBuilder();
        if (MapUtils.isNotEmpty(methodInfoVO.getMethodParameter())) {
            for (Map.Entry<String, String> methodParameter : methodInfoVO.getMethodParameter().entrySet()) {
                String simpleName = methodParameter.getKey();
                comment.append("     * @param ").append(simpleName).append(" ").append(TranslatorFactroy.translate(simpleName)).append("\n");
            }
        }
        String returnType = new ClazzInfoVO().getClazzSimpleType(methodInfoVO.getMethodReturn());
        if (StringUtils.isNotEmpty(returnType) && !"void".equals(returnType)) {
            if (JavaTypeEnum.isBasic(returnType)) {
                comment.append("     * @return ").append(returnType).append("\n");
            } else {
                comment.append("     * @return {@link ").append(returnType).append("}\n");
            }
        }
        if (CollectionUtils.isNotEmpty(methodInfoVO.getMethodThrowsList())) {
            for (String methodThrow : methodInfoVO.getMethodThrowsList()) {
                comment.append("     * @throws ").append(methodThrow).append(" ").append(TranslatorFactroy.translate(methodThrow)).append("\n");
            }
        }
        return String.format(methodComment, translate, comment);
    }

    public static String getFieldComment(String fieldName) {
        if (fieldName == null) {
            return StringUtils.EMPTY;
        }
        String translate = TranslatorFactroy.translate(fieldName);
        return String.format(fieldComment, translate);
    }

    public static String getLocalComment(String localName, int distance) {
        if (StringUtils.isEmpty(localName)) {
            return "";
        }
        StringBuilder blankSpace = new StringBuilder();
        for (int i = 0; i < distance; i++) {
            blankSpace.append(" ");
        }
        String translate = TranslatorFactroy.translate(localName);
        return String.format(localComment, blankSpace, translate);
    }

    public static ClazzInfoVO getClazzInfoVO(PsiClass psiClass) {
        List<ClazzInfoVO> clazzInfoVOS = getClazzInfoVOS(new PsiClass[]{psiClass});
        if (CollectionUtils.isEmpty(clazzInfoVOS)) {
            return new ClazzInfoVO();
        }
        return clazzInfoVOS.get(0);
    }

    public static List<ClazzInfoVO> getClazzInfoVOS(Project project, String clazzName) {
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(clazzName, GlobalSearchScope.projectScope(project));
        return getClazzInfoVOS(psiClasses);
    }

    public static void setClazzInfoDOVO(Project project, List<ClazzInfoVO> clazzInfoVOS) {
        for (ClazzInfoVO clazzInfoVO : clazzInfoVOS) {
            ClazzInfoDOVO clazzInfoDOVO = new ClazzInfoDOVO();
            for (MethodInfoVO methodInfo : clazzInfoVO.getMethodInfos()) {
                if (clazzInfoDOVO.getClazzName() != null && clazzInfoDOVO.getClazzVOName() != null) {
                    break;
                }
                setDOVOClazz(project, clazzInfoVO, clazzInfoDOVO, methodInfo.getMethodReturn());
                Map<String, String> methodParameter = methodInfo.getMethodParameter();
                methodParameter.values().forEach(methodParam -> {
                    setDOVOClazz(project, clazzInfoVO, clazzInfoDOVO, methodParam);
                });
            }
            clazzInfoVO.setClazzInfoDOVO(clazzInfoDOVO);
        }
    }

    private static void setDOVOClazz(Project project, ClazzInfoVO clazzInfoVO, ClazzInfoDOVO clazzInfoDOVO, String methodReturn) {
        if (clazzInfoDOVO.getClazzName() == null && methodReturn.endsWith("DO")) {
            clazzInfoDOVO.setClazzName(methodReturn);
            String tableName = StringUtil.textToConstant(clazzInfoVO.getSimpleName(methodReturn.replace("DO", "")));
            clazzInfoDOVO.setTableName(tableName);
            List<ClazzInfoVO> clazzInfoVOSList = JavaDocConfig.getClazzInfoVOS(project, clazzInfoVO.getClazzSimpleType(methodReturn));
            if (clazzInfoVOSList.size() > 0) {
                clazzInfoDOVO.setFieldinfos(clazzInfoVOSList.get(0).getFieldinfos());
            }
        }
        if (clazzInfoDOVO.getClazzVOName() == null && methodReturn.endsWith("VO")) {
            clazzInfoDOVO.setClazzVOName(methodReturn);
            String tableName = StringUtil.textToConstant(clazzInfoVO.getSimpleName(methodReturn.replace("VO", "")));
            clazzInfoDOVO.setTableName(tableName);
            List<ClazzInfoVO> clazzInfoVOSList = JavaDocConfig.getClazzInfoVOS(project, clazzInfoVO.getClazzSimpleType(methodReturn));
            if (clazzInfoVOSList.size() > 0) {
                clazzInfoDOVO.setVosFieldinfos(clazzInfoVOSList.get(0).getFieldinfos());
            }
        }
    }

    public static List<ClazzInfoVO> getClazzInfoVOS(PsiClass[] psiClasses) {
        List<ClazzInfoVO> clazzInfoVOS = new ArrayList<>();
        if (psiClasses.length == 0) {
            return clazzInfoVOS;
        }
        for (PsiClass psiClass : psiClasses) {
            try {
                ClazzInfoVO clazzInfoVO = new ClazzInfoVO();
                clazzInfoVOS.add(clazzInfoVO);
                clazzInfoVO.setPsiClass(psiClass);
                clazzInfoVO.setClazzName(psiClass.getName());
                clazzInfoVO.setMethodInfos(getMethodInfoVOS(psiClass.getAllMethods()));
                clazzInfoVO.setFieldinfos(getFieldInfoVOS(psiClass.getAllFields()));
                clazzInfoVO.addClazzImports();
            } catch (Exception e) {
                NoticeUtil.error(e);
            }
        }
        return clazzInfoVOS;
    }

    public static MethodInfoVO getMethodInfoVO(PsiMethod psiMethod) {
        List<MethodInfoVO> methodInfoVOS = getMethodInfoVOS(new PsiMethod[]{psiMethod});
        if (CollectionUtils.isEmpty(methodInfoVOS)) {
            return new MethodInfoVO();
        }
        return methodInfoVOS.get(0);
    }

    public static List<MethodInfoVO> getMethodInfoVOS(PsiMethod[] psiMethods) {
        List<MethodInfoVO> methodInfoVOS = new ArrayList<>();
        if (psiMethods.length == 0) {
            return methodInfoVOS;
        }
        for (PsiMethod psiMethod : psiMethods) {
            if (!psiMethod.isWritable() || psiMethod.isConstructor()) {
                continue;
            }
            MethodInfoVO methodInfoVO = new MethodInfoVO();
            methodInfoVOS.add(methodInfoVO);
            methodInfoVO.setPsiMethod(psiMethod);
            methodInfoVO.setMethodName(psiMethod.getName());
            if (psiMethod.getReturnType() != null) {
                methodInfoVO.setMethodReturn(psiMethod.getReturnType().getCanonicalText());
            }
            PsiParameterList parameterList = psiMethod.getParameterList();
            PsiParameter[] parameters = parameterList.getParameters();
            for (PsiParameter parameter : parameters) {
                if (parameter != null) {
                    methodInfoVO.getMethodParameter().put(parameter.getName(), parameter.getType().getCanonicalText());
                    methodInfoVO.getMethodParameterInfos().add(parameter.getText());
                }
            }
            PsiReferenceList throwsList = psiMethod.getThrowsList();
            for (PsiElement child : throwsList.getChildren()) {
                if (child instanceof PsiJavaCodeReferenceElement) {
                    methodInfoVO.addMethodThrowsList(child.getText());
                }
            }
        }
        return methodInfoVOS;
    }

    public static FieldInfoVO getFieldInfoVO(PsiField psiField) {
        List<FieldInfoVO> fieldInfoVOS = getFieldInfoVOS(new PsiField[]{psiField});
        if (CollectionUtils.isEmpty(fieldInfoVOS)) {
            return new FieldInfoVO();
        }
        return fieldInfoVOS.get(0);
    }

    public static List<FieldInfoVO> getFieldInfoVOS(PsiField[] psiFields) {
        List<FieldInfoVO> fieldInfoVOS = new ArrayList<>();
        if (psiFields.length == 0) {
            return fieldInfoVOS;
        }
        for (PsiField psiField : psiFields) {
            FieldInfoVO fieldInfoVO = new FieldInfoVO();
            fieldInfoVOS.add(fieldInfoVO);
            fieldInfoVO.setPsiField(psiField);
            fieldInfoVO.setFieldName(psiField.getName());
        }
        return fieldInfoVOS;
    }

}
