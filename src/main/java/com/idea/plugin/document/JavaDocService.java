package com.idea.plugin.document;

import com.idea.plugin.document.support.*;
import com.idea.plugin.orm.support.enums.FileTypeEnum;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.ui.JavaDocUI;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class JavaDocService {
    private static final WriterService writerService = new WriterService();


    public void generationFile(ActionContext context) {
        try {
            PsiElement psiElement = context.getPsiElement();
            if (context.getEditor() == null && context.getVirtualFiles().length > 0) {
                JavaDocUI javaDocUI = JavaDocUI.getInstance(context.getProject());
                if (javaDocUI.showAndGet()) {
                    JavaDocVO javaDocVO = javaDocUI.getJavaDocVO();
                    writerMultiFileDoc(context, javaDocVO, context.getVirtualFiles());
                }
            }
            if (psiElement == null || psiElement.getNode() == null || context.getEditor() == null) {
                return;
            }
            SelectionModel selectionModel = context.getEditor().getSelectionModel();
            List<Caret> allCarets = context.getEditor().getCaretModel().getAllCarets();
            if (allCarets.size() == 1) {
                VirtualFile virtualFile = context.getVirtualFiles()[0];
                String fileName = virtualFile.getName().substring(0, virtualFile.getName().lastIndexOf("."));
                List<ClazzInfoVO> clazzInfoVOS = JavaDocConfig.getClazzInfoVOS(context.getProject(), fileName);
                if (CollectionUtils.isEmpty(clazzInfoVOS)) {
                    return;
                }
                ClazzInfoVO sourceClazzInfoVO = clazzInfoVOS.get(0);
                if (psiElement instanceof PsiClass) {
                    PsiClass[] innerClasses = sourceClazzInfoVO.getPsiClass().getInnerClasses();
                    Optional<String> isInnerClazzOptional = Arrays.stream(innerClasses).map(NavigationItem::getName).filter(Objects::nonNull).filter(s -> s.equals(((PsiClass) psiElement).getName())).findAny();
                    if (sourceClazzInfoVO.getClazzName().equals(((PsiClass) psiElement).getName()) || isInnerClazzOptional.isPresent()) {
                        JavaDocUI javaDocUI = JavaDocUI.getInstance(context.getProject());
                        if (javaDocUI.showAndGet()) {
                            JavaDocVO javaDocVO = javaDocUI.getJavaDocVO();
                            List<String> classNameList = new ArrayList<>();
                            ClazzInfoVO clazzInfoVO = JavaDocConfig.getClazzInfoVO((PsiClass) psiElement);
                            writeClazzComment(context, clazzInfoVO, javaDocVO, classNameList);
                        }
                    } else {
                        writerComment(context, ((PsiClass) psiElement).getName());
                    }
                } else if (psiElement instanceof PsiMethod) {
                    if (sourceClazzInfoVO.getClazzName().equals(((PsiClass) psiElement.getParent()).getName())) {
                        MethodInfoVO methodInfoVO = JavaDocConfig.getMethodInfoVO((PsiMethod) psiElement);
                        String methodComment = JavaDocConfig.getMethodComment(methodInfoVO);
                        writerService.writeDoc(context, methodInfoVO.psiMethod, methodComment, true);
                    } else {
                        writerComment(context, ((PsiMethod) psiElement).getName());
                    }
                } else if (psiElement instanceof PsiField) {
                    if (sourceClazzInfoVO.getClazzName().equals(((PsiClass) psiElement.getParent()).getName())) {
                        FieldInfoVO fieldInfoVO = JavaDocConfig.getFieldInfoVO((PsiField) psiElement);
                        if (fieldInfoVO != null) {
                            String fieldComment = JavaDocConfig.getFieldComment(fieldInfoVO.getFieldName());
                            writerService.writeDoc(context, fieldInfoVO.psiField, fieldComment, true);
                        }
                    } else {
                        writerComment(context, ((PsiField) psiElement).getName());
                    }
                } else if (psiElement instanceof PsiLocalVariable) {
                    PsiLocalVariable psiLocalVariable = (PsiLocalVariable) psiElement;
                    String localName = psiLocalVariable.getName();
                    writerComment(context, localName);
                } else {
                    String selectedText = selectionModel.getSelectedText();
                    if (StringUtils.isEmpty(selectedText)) {
                        selectedText = allCarets.get(0).getSelectedText();
                    }
                    writerComment(context, selectedText);
                }
            } else {
                int lineNumberCurrent = context.getDocument().getLineNumber(context.getOffset());
                int lineStartOffset = context.getDocument().getLineStartOffset(lineNumberCurrent);
                PsiElement elementAt = context.getPsiFile().findElementAt(lineStartOffset);
                int distance = 0;
                int offset = lineStartOffset;
                while (null == elementAt || elementAt instanceof PsiWhiteSpace) {
                    elementAt = context.getPsiFile().findElementAt(++offset);
                    ++distance;
                }
                StringBuilder allComment = new StringBuilder();
                for (Caret caret : allCarets) {
                    String selectedText = caret.getSelectedText();
                    allComment.append(JavaDocConfig.getLocalComment(selectedText, distance));
                }
                if (StringUtils.isEmpty(allComment)) {
                    return;
                }
                WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                    context.getDocument().insertString(lineStartOffset, allComment);
                    context.getEditor().getCaretModel().moveToOffset(lineStartOffset + allComment.length() - 1);
                    context.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    private void writerMultiFileDoc(ActionContext context, JavaDocVO javaDocVO, VirtualFile[] virtualFiles) {
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                writerMultiFileDoc(context, javaDocVO, virtualFile.getChildren());
            } else {
                String fileName = virtualFile.getName();
                int index = fileName.lastIndexOf(".");
                if (index > 0) {
                    String typename = fileName.substring(index + 1);
                    if (FileTypeEnum.JAVA.getType().equals(typename.toLowerCase())) {
                        fileName = fileName.substring(0, index);
                        List<ClazzInfoVO> clazzInfoVOS = JavaDocConfig.getClazzInfoVOS(context.getProject(), fileName);
                        if (CollectionUtils.isNotEmpty(clazzInfoVOS)) {
                            for (ClazzInfoVO clazzInfoVO : clazzInfoVOS) {
                                List<String> classNameList = new ArrayList<>();
                                writeClazzComment(context, clazzInfoVO, javaDocVO, classNameList);
                            }
                        }
                    }
                }
            }
        }
    }

    private void writerComment(ActionContext context, String text) {
        int lineNumberCurrent = context.getDocument().getLineNumber(context.getOffset());
        int lineStartOffset = context.getDocument().getLineStartOffset(lineNumberCurrent);
        PsiElement elementAt = context.getPsiFile().findElementAt(lineStartOffset);
        int distance = 0;
        int offset = lineStartOffset;
        while (null == elementAt || elementAt instanceof PsiWhiteSpace) {
            elementAt = context.getPsiFile().findElementAt(++offset);
            ++distance;
        }
        String comment = JavaDocConfig.getLocalComment(text, distance);
        if (StringUtils.isEmpty(comment)) {
            return;
        }
        WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
            context.getDocument().insertString(lineStartOffset, comment);
            context.getEditor().getCaretModel().moveToOffset(lineStartOffset + comment.length() - 1);
            context.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        });
    }

    private void writeClazzComment(ActionContext context, ClazzInfoVO clazzInfoVO, JavaDocVO javaDocVO, List<String> classNameList) {
        if (javaDocVO == null) {
            return;
        }
        if (Boolean.TRUE.equals(javaDocVO.getCGenerate())) {
            String clazzComment = JavaDocConfig.getClazzComment(clazzInfoVO.getClazzName());
            writerService.writeDoc(context, clazzInfoVO.psiClass, clazzComment, javaDocVO.getCCovered());
            if (Boolean.TRUE.equals(javaDocVO.getCParent())) {
                PsiClass superPsiClass = clazzInfoVO.psiClass;
                classNameList.add(superPsiClass.getName());
                while (!"Object".equals(superPsiClass.getName()) && superPsiClass.isWritable()) {
                    superPsiClass = superPsiClass.getSuperClass();
                    if (null == superPsiClass || classNameList.contains(superPsiClass.getName())) {
                        break;
                    }
                    classNameList.add(superPsiClass.getName());
                    ClazzInfoVO superClazzInfoVO = JavaDocConfig.getClazzInfoVO(superPsiClass);
                    writeClazzComment(context, superClazzInfoVO, javaDocVO, classNameList);
                }
            }
            if (Boolean.TRUE.equals(javaDocVO.getCInner())) {
                PsiClass[] innerClasses = clazzInfoVO.psiClass.getInnerClasses();
                if (innerClasses.length > 0) {
                    for (PsiClass innerClass : innerClasses) {
                        classNameList.add(innerClass.getName());
                        ClazzInfoVO innerClazzInfoVO = JavaDocConfig.getClazzInfoVO(innerClass);
                        writeClazzComment(context, innerClazzInfoVO, javaDocVO, classNameList);
                    }
                }
            }
        }
        if (Boolean.TRUE.equals(javaDocVO.getMGenerate())) {
            List<MethodInfoVO> methodInfos = clazzInfoVO.getMethodInfos();
            if (CollectionUtils.isNotEmpty(methodInfos)) {
                if (!Boolean.TRUE.equals(javaDocVO.getMParent())) {
                    PsiMethod[] methods = clazzInfoVO.psiClass.getAllMethods();
                    if (methods.length > 0) {
                        List<String> curMethodNames = Arrays.stream(methods).map(PsiMethod::getName).collect(Collectors.toList());
                        methodInfos = methodInfos.stream().filter(methodInfoVO -> curMethodNames.contains(methodInfoVO.getMethodName())).collect(Collectors.toList());
                    } else {
                        methodInfos = new ArrayList<>();
                    }
                }
                for (MethodInfoVO methodInfoVO : methodInfos) {
                    String methodComment = JavaDocConfig.getMethodComment(methodInfoVO);
                    writerService.writeDoc(context, methodInfoVO.psiMethod, methodComment, javaDocVO.getMCovered());
                }
                if (Boolean.TRUE.equals(javaDocVO.getMInner())) {
                    PsiClass[] innerClasses = clazzInfoVO.psiClass.getInnerClasses();
                    if (innerClasses.length > 0) {
                        for (PsiClass innerClass : innerClasses) {
                            ClazzInfoVO innerClazzInfoVO = JavaDocConfig.getClazzInfoVO(innerClass);
                            if (CollectionUtils.isNotEmpty(innerClazzInfoVO.getMethodInfos())) {
                                for (MethodInfoVO methodInfoVO : innerClazzInfoVO.getMethodInfos()) {
                                    String methodComment = JavaDocConfig.getMethodComment(methodInfoVO);
                                    writerService.writeDoc(context, methodInfoVO.psiMethod, methodComment, javaDocVO.getMCovered());
                                }
                            }
                        }
                    }
                }

            }
        }
        if (Boolean.TRUE.equals(javaDocVO.getFGenerate())) {
            List<FieldInfoVO> fieldinfos = clazzInfoVO.getFieldinfos();
            if (CollectionUtils.isNotEmpty(fieldinfos)) {
                if (!Boolean.TRUE.equals(javaDocVO.getFParent())) {
                    PsiField[] fields = clazzInfoVO.psiClass.getFields();
                    if (fields.length > 0) {
                        List<String> curFieldNames = Arrays.stream(fields).map(PsiField::getName).collect(Collectors.toList());
                        fieldinfos = fieldinfos.stream().filter(fieldInfoVO -> curFieldNames.contains(fieldInfoVO.getFieldName())).collect(Collectors.toList());
                    } else {
                        fieldinfos = new ArrayList<>();
                    }
                }
                for (FieldInfoVO fieldInfoVO : fieldinfos) {
                    String fieldComment = JavaDocConfig.getFieldComment(fieldInfoVO.getFieldName());
                    writerService.writeDoc(context, fieldInfoVO.psiField, fieldComment, javaDocVO.getFCovered());
                }
                if (Boolean.TRUE.equals(javaDocVO.getFInner())) {
                    PsiClass[] innerClasses = clazzInfoVO.psiClass.getInnerClasses();
                    if (innerClasses.length > 0) {
                        for (PsiClass innerClass : innerClasses) {
                            ClazzInfoVO innerClazzInfoVO = JavaDocConfig.getClazzInfoVO(innerClass);
                            if (CollectionUtils.isNotEmpty(innerClazzInfoVO.getFieldinfos())) {
                                for (FieldInfoVO fieldInfoVO : innerClazzInfoVO.getFieldinfos()) {
                                    String methodComment = JavaDocConfig.getFieldComment(fieldInfoVO.getFieldName());
                                    writerService.writeDoc(context, fieldInfoVO.psiField, methodComment, javaDocVO.getMCovered());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
