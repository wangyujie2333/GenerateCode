package com.idea.plugin.document;

import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class WriterService {

    private static final WriterService writerService = new WriterService();

    public static WriterService getInstance() {
        return writerService;
    }

    public void writeConstructor(ActionContext context, PsiElement psiElement) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());

                PsiMethod psiMethod = psiElementFactory.createConstructor();
                psiElement.add(psiMethod);
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeConstructor(ActionContext context, PsiElement psiElement, List<PsiField> psiFields) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
                PsiMethod constructor = psiElementFactory.createConstructor();
                PsiUtil.setModifierProperty(constructor, PsiModifier.PUBLIC, true);
                PsiCodeBlock builderConstructorBody = constructor.getBody();
                if (builderConstructorBody != null) {
                    for (PsiField field : psiFields) {
                        PsiType fieldType = field.getType();
                        String fieldName = field.getName();
                        PsiParameter parameter = psiElementFactory.createParameter(fieldName, fieldType);
                        constructor.getParameterList().add(parameter);
                        PsiStatement assignStatement = psiElementFactory.createStatementFromText(String.format("this.%1$s = %1$s;", fieldName), constructor);
                        builderConstructorBody.add(assignStatement);
                    }
                }
                psiElement.add(constructor);
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeMethod(ActionContext context, PsiElement psiElement, String methodStr) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
                PsiMethod psiMethod = psiElementFactory.createMethodFromText(methodStr, psiElement);
                psiElement.add(psiMethod);
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeMethodBefore(ActionContext context, PsiElement psiElement, PsiMethod prePsiMethod, String methodStr) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
                PsiMethod psiMethod = psiElementFactory.createMethodFromText(methodStr, psiElement);
                psiElement.addBefore(psiMethod, prePsiMethod);
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeMethodAfter(ActionContext context, PsiElement psiElement, PsiMethod prePsiMethod, String methodStr) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
                PsiMethod psiMethod = psiElementFactory.createMethodFromText(methodStr, psiElement);
                psiElement.addAfter(psiMethod, prePsiMethod);
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void replaceMethod(ActionContext context, PsiElement psiElement, String methodStr) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
                PsiMethod psiMethod = psiElementFactory.createMethodFromText(methodStr, psiElement);
                psiElement.replace(psiMethod);
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeField(ActionContext context, PsiElement psiElement, String fieldStr) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
                PsiField psiField = psiElementFactory.createFieldFromText(fieldStr, null);
                psiElement.add(psiField);
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeImport(ActionContext context, PsiElement psiElement, String fieldStr) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeDoc(ActionContext context, PsiElement psiElement, String comment, Boolean cCovered) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                if (psiElement == null || psiElement.getContainingFile() == null || psiElement.getNode() == null || StringUtils.isEmpty(comment)) {
                    return;
                }
                // 写入文档注释
                if (psiElement instanceof PsiJavaDocumentedElement) {
                    PsiDocComment psiDocComment = ((PsiJavaDocumentedElement) psiElement).getDocComment();
                    PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(context.getProject());
                    PsiDocComment docComment = psiElementFactory.createDocCommentFromText(comment);
                    Boolean isComment = false;
                    if (psiDocComment == null) {
                        isComment = true;
                        psiElement.getNode().addChild(docComment.getNode(), psiElement.getFirstChild().getNode());
                    } else if (Boolean.TRUE.equals(cCovered)) {
                        isComment = true;
                        psiDocComment.replace(docComment);
                    }
                    // 格式化文档注释
                    if (isComment) {
                        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiElement.getProject());
                        PsiElement javadocElement = psiElement.getFirstChild();
                        int startOffset = javadocElement.getTextOffset();
                        int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
                        codeStyleManager.reformatText(psiElement.getContainingFile(), startOffset, endOffset + 1);
                    }
                }
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }

    public void writeString(ActionContext context, String text) {
        try {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                int start = context.getEditor().getSelectionModel().getSelectionStart();
                EditorModificationUtil.insertStringAtCaret(context.getEditor(), text);
                context.getEditor().getSelectionModel().setSelection(start, start + text.length());
            });
        } catch (Throwable e) {
            NoticeUtil.error(e);
        }
    }
}
