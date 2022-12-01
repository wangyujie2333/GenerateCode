package com.idea.plugin.method;

import com.idea.plugin.document.WriterService;
import com.idea.plugin.document.support.ClazzInfoVO;
import com.idea.plugin.document.support.JavaDocConfig;
import com.idea.plugin.document.support.JavaSetGetVO;
import com.idea.plugin.orm.support.enums.FileTypeEnum;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.ui.JavaSetGetUI;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataGenerateService {

    public void doGenerate(ActionContext context) {
        PsiElement psiElement = context.getPsiElement();
        if (psiElement instanceof PsiClass) {
            VirtualFile virtualFile = context.getVirtualFiles()[0];
            String fileName = virtualFile.getName().substring(0, virtualFile.getName().lastIndexOf("."));
            PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(fileName, GlobalSearchScope.projectScope(context.getProject()));
            if (psiClasses.length == 0) {
                return;
            }
            PsiClass[] innerClasses = psiClasses[0].getInnerClasses();
            Optional<String> isInnerClazzOptional = Arrays.stream(innerClasses).map(NavigationItem::getName).filter(Objects::nonNull).filter(s -> s.equals(((PsiClass) psiElement).getName())).findAny();
            if (fileName.equals(((PsiClass) psiElement).getName()) || isInnerClazzOptional.isPresent()) {
                JavaSetGetUI javaSetGetUI = JavaSetGetUI.getInstance(context.getProject());
                JavaSetGetVO javaSetGetVO = javaSetGetUI.getJavaSetGetVO();
                writeClassSetGetMethod(context, javaSetGetVO, (PsiClass) psiElement);
            }
        } else if (context.getEditor() == null && context.getVirtualFiles().length > 0) {
            if (context.getEditor() == null && context.getVirtualFiles().length > 0) {
                JavaSetGetUI javaSetGetUI = JavaSetGetUI.getInstance(context.getProject());
                if (javaSetGetUI.showAndGet()) {
                    JavaSetGetVO javaSetGetVO = javaSetGetUI.getJavaSetGetVO();
                    writerMultiFileData(context, javaSetGetVO, context.getVirtualFiles());
                }
            }
        }
    }

    private void writerMultiFileData(ActionContext context, JavaSetGetVO javaSetGetVO, VirtualFile[] virtualFiles) {
        if (virtualFiles.length == 0) {
            return;
        }
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                writerMultiFileData(context, javaSetGetVO, virtualFile.getChildren());
            } else {
                String fileName = virtualFile.getName();
                if (!fileName.contains(".")) {
                    continue;
                }
                int index = fileName.lastIndexOf(".");
                String typename = fileName.substring(index + 1);
                if (!FileTypeEnum.JAVA.getType().equals(typename.toLowerCase())) {
                    continue;
                }
                fileName = fileName.substring(0, index);

                PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(fileName, GlobalSearchScope.projectScope(context.getProject()));
                if (psiClasses.length > 0) {
                    writeClassSetGetMethod(context, javaSetGetVO, psiClasses[0]);
                }
            }
        }
    }

    private void writeClassSetGetMethod(ActionContext context, JavaSetGetVO javaSetGetVO, PsiClass psiClass) {
        if (javaSetGetVO.getIsSGParent()) {
            PsiClass superPsiClass = psiClass;
            while (!"Object".equals(superPsiClass.getName()) && superPsiClass.isWritable()) {
                superPsiClass = superPsiClass.getSuperClass();
                if (null == superPsiClass) {
                    break;
                }
                writeClassSetGetMethod(context, javaSetGetVO, superPsiClass);
            }
        }
        if (Boolean.TRUE.equals(javaSetGetVO.getIsSGInner())) {
            PsiClass[] innerClasses = psiClass.getInnerClasses();
            if (innerClasses.length > 0) {
                for (PsiClass innerClass : innerClasses) {
                    writeClassSetGetMethod(context, javaSetGetVO, innerClass);
                }
            }
        }
        boolean isEnum = psiClass.getSuperClassType().getName().equals("Enum");
        if (javaSetGetVO.getIsNoneConstructor() && !isEnum) {
            if (!hasNoneConstructor(psiClass)) {
                WriterService.getInstance().writeConstructor(context, psiClass);
            }
        }
        if (javaSetGetVO.getIsAllConstructor() || isEnum) {
            if (!hasAllConstructor(psiClass)) {
                WriterService.getInstance().writeConstructor(context, psiClass, getFields(psiClass));
            }
        }
        List<String> methodNames = getMethodNames(psiClass);
        Map<String, PsiMethod> methodNameMap = getMethodNameMap(psiClass);
        List<String> fieldNames = getFieldNames(psiClass);
        Map<String, PsiField> fieldNameMap = getFieldNameMap(psiClass);
        ClazzInfoVO clazzInfoVO = new ClazzInfoVO();
        List<String> getNames = fieldNames.stream().map(fieldName -> "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)).collect(Collectors.toList());
        boolean hasgetSet = methodNameMap.keySet().stream().anyMatch(getNames::contains);
        Map<String, PsiMethod> afterMethods = new LinkedHashMap<>();
        for (int i = 0; i < fieldNames.size(); i++) {
            String preSetName = null;
            String preGetName = null;
            String nextSetName = null;
            String nextGetName = null;
            int j = i - 1;
            while (j >= 0) {
                String preMethodName = fieldNames.get(j).substring(0, 1).toUpperCase() + fieldNames.get(j).substring(1);
                preSetName = "set" + preMethodName;
                preGetName = "get" + preMethodName;
                if (methodNames.contains(preGetName)) {
                    break;
                }
                j--;
            }
            int k = i + 1;
            while (k < fieldNames.size()) {
                String nextMethodName = fieldNames.get(k).substring(0, 1).toUpperCase() + fieldNames.get(k).substring(1);
                nextSetName = "set" + nextMethodName;
                nextGetName = "get" + nextMethodName;
                if (methodNames.contains(nextGetName)) {
                    break;
                }
                k++;
            }
            String fieldName = fieldNames.get(i);
            String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            String setName = "set" + methodName;
            String getName = "get" + methodName;
            String type = clazzInfoVO.getClazzSimpleType(fieldNameMap.get(fieldName).getType().getCanonicalText());
            if (type.contains(".")) {
                type = type.substring(type.lastIndexOf(".") + 1);
            }
            String getStr = JavaDocConfig.getGetMethodStr(type, fieldName);
            String setStr = JavaDocConfig.getSetMethodStr(type, fieldName);
            if (!hasgetSet) {
                if (javaSetGetVO.getIsGet() || isEnum) {
                    if (!methodNames.contains(getName)) {
                        WriterService.getInstance().writeMethod(context, psiClass, getStr);
                    }
                }
                if (javaSetGetVO.getIsSet() && !isEnum) {
                    if (!methodNames.contains(setName)) {
                        WriterService.getInstance().writeMethod(context, psiClass, setStr);
                    }
                }
            } else {
                if (methodNameMap.containsKey(preSetName)) {
                    if (javaSetGetVO.getIsGet() || isEnum) {
                        if (!methodNames.contains(getName)) {
                            afterMethods.put(getStr, methodNameMap.get(preSetName));
                        }
                    }
                    if (javaSetGetVO.getIsSet() && !isEnum) {
                        if (!methodNames.contains(setName)) {
                            afterMethods.put(setStr, methodNameMap.get(preSetName));
                        }
                    }
                } else if (methodNameMap.containsKey(nextGetName)) {
                    if (javaSetGetVO.getIsGet() || isEnum) {
                        if (!methodNames.contains(getName)) {
                            WriterService.getInstance().writeMethodBefore(context, psiClass, methodNameMap.get(nextGetName), getStr);
                        }
                    }
                    if (javaSetGetVO.getIsSet() && !isEnum) {
                        if (!methodNames.contains(setName)) {
                            WriterService.getInstance().writeMethodBefore(context, psiClass, methodNameMap.get(nextGetName), setStr);
                        }
                    }
                } else {
                    Optional<PsiMethod> psiMethodOptional = Arrays.stream(psiClass.getMethods()).filter(psiMethod -> psiMethod.isWritable() && !psiMethod.isConstructor()).findFirst();
                    if (psiMethodOptional.isPresent()) {
                        if (javaSetGetVO.getIsGet() || isEnum) {
                            if (!methodNames.contains(getName)) {
                                WriterService.getInstance().writeMethodBefore(context, psiClass, psiMethodOptional.get(), getStr);
                            }
                        }
                        if (javaSetGetVO.getIsSet() && !isEnum) {
                            if (!methodNames.contains(setName)) {
                                WriterService.getInstance().writeMethodBefore(context, psiClass, psiMethodOptional.get(), setStr);
                            }
                        }
                    }
                }
            }
        }
        if (afterMethods.size() > 0) {
            List<Map.Entry<String, PsiMethod>> afterMethodsList = new ArrayList<>(afterMethods.entrySet());
            for (int i = afterMethodsList.size() - 1; i >= 0; i--) {
                Map.Entry<String, PsiMethod> afterMethod = afterMethodsList.get(i);
                WriterService.getInstance().writeMethodAfter(context, psiClass, afterMethod.getValue(), afterMethod.getKey());
            }
        }
    }


    private Boolean hasNoneConstructor(PsiClass psiClass) {
        return Arrays.stream(psiClass.getMethods()).anyMatch(psiMethod -> psiMethod.isConstructor() && psiMethod.getParameterList().getParametersCount() == 0);
    }

    private Boolean hasAllConstructor(PsiClass psiClass) {
        return Arrays.stream(psiClass.getMethods()).anyMatch(psiMethod -> psiMethod.isConstructor() && psiMethod.getParameterList().getParametersCount() >= getFields(psiClass).size());
    }

    private List<String> getMethodNames(PsiClass psiClass) {
        return Arrays.stream(psiClass.getMethods()).filter(psiMethod -> psiMethod.isWritable() && !psiMethod.isConstructor()).map(PsiMethod::getName).collect(Collectors.toList());
    }

    private Map<String, PsiMethod> getMethodNameMap(PsiClass psiClass) {
        return Arrays.stream(psiClass.getMethods()).filter(psiMethod -> psiMethod.isWritable() && !psiMethod.isConstructor()).collect(Collectors.toMap(PsiMethod::getName, Function.identity(), (o, n) -> o));
    }

    private List<PsiField> getFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields()).filter(psiField -> !(psiField instanceof PsiEnumConstant
                || psiField.hasModifierProperty(PsiModifier.STATIC)
                || psiField.hasModifierProperty(PsiModifier.FINAL))).collect(Collectors.toList());
    }

    private Map<String, PsiField> getFieldNameMap(PsiClass psiClass) {
        return getFields(psiClass).stream().collect(Collectors.toMap(PsiField::getName, Function.identity()));
    }


    private List<String> getFieldNames(PsiClass psiClass) {
        return getFields(psiClass).stream().map(PsiField::getName).collect(Collectors.toList());
    }

}
