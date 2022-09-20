package com.idea.plugin.methodgenerate;

import com.idea.plugin.methodgenerate.module.ObjectConfigDO;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.utils.ClipboardUtils;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetGenerateService {

    int distance = 0;

    public void doGenerate(ActionContext context) {
        ObjectConfigDO setObjConfigDO = this.getSetObjConfigDO(context);
        if (setObjConfigDO != null) {
            ObjectConfigDO getObjConfigDO = this.getObjConfigDOByClipboardText(context);
            if (getObjConfigDO == null) {
                getObjConfigDO = setObjConfigDO;
            }
            this.weavingSetGetCode(context, setObjConfigDO, getObjConfigDO);
        }
    }

    public ObjectConfigDO getSetObjConfigDO(ActionContext context) {
        distance = 0;
        PsiClass psiClass = null;
        PsiFile psiFile = null;
        String clazzName = null;
        String clazzParamName = null;
        PsiElement psiElement = context.getPsiElement();
        // 鼠标定位到类
        if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) context.getPsiElement();
            // 通过光标步长递进找到属性名称
            psiFile = context.getPsiFile();
            Editor editor = context.getEditor();
            int offsetStep = context.getOffset() + 1;

            clazzName = psiFile.getName();
            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt || elementAt.getText().equals(psiClass.getName()) || elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(++offsetStep);
            }
            clazzParamName = elementAt.getText();
        }
        // 鼠标定位到属性
        if (psiElement instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) psiElement;
            clazzParamName = psiLocalVariable.getName();
            // 通过光标步长递进找到属性名称
            psiFile = context.getPsiFile();
            Editor editor = context.getEditor();
            int offsetStep = context.getOffset() - 1;

            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt || elementAt.getText().equals(clazzParamName) || elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(--offsetStep);
            }
            clazzName = elementAt.getText();
            PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(clazzName, GlobalSearchScope.projectScope(context.getProject()));
            if (psiClasses.length > 0) {
                psiClass = psiClasses[0];
            }
        }
        if (psiClass == null) {
            return null;
        }
        int lineNumberCurrent = context.getDocument().getLineNumber(context.getOffset());
        int lineStartOffset = context.getDocument().getLineStartOffset(lineNumberCurrent);
        PsiElement elementAt = context.getPsiFile().findElementAt(lineStartOffset);
        while (null == elementAt || elementAt.getText().equals(psiFile.getName()) || elementAt instanceof PsiWhiteSpace) {
            elementAt = psiFile.findElementAt(++lineStartOffset);
            ++distance;
        }

        return getObjectConfigDO(psiClass, clazzName, clazzParamName);
    }

    private ObjectConfigDO getObjectConfigDO(PsiClass psiClass, String clazzName, String clazzParamName) {
        ObjectConfigDO objectConfigDO = new ObjectConfigDO(clazzName, clazzParamName);
        PsiField[] allFields = psiClass.getAllFields();
        if (allFields.length > 0) {
            for (PsiField field : allFields) {
                if (field instanceof PsiEnumConstant) {
                    continue;
                }
                if (field.hasModifierProperty(PsiModifier.STATIC) || field.hasModifierProperty(PsiModifier.FINAL)) {
                    continue;
                }
                objectConfigDO.setParam(field.getName());
            }
        }
        return objectConfigDO;
    }

    protected ObjectConfigDO getObjConfigDOByClipboardText(ActionContext context) {
        String systemClipboardText = ClipboardUtils.getClipboardText().trim();
        String[] split = systemClipboardText.split("\\s");
        String clazzName = "";
        String clazzParam = "";
        if (split.length == 1) {
            if (StringUtils.isNotEmpty(split[0].trim())) {
                if (split[0].trim().charAt(0) >= 'A') {
                    clazzName = split[0].trim();
                    clazzParam = clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);
                } else {
                    clazzParam = split[0].trim();
                    clazzName = clazzParam.substring(0, 1).toLowerCase() + clazzParam.substring(1);
                }
            }
        } else {
            clazzName = split[0].trim();
            clazzParam = split[1].trim();
        }
        // 获取类
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(clazzName, GlobalSearchScope.projectScope(context.getProject()));
        if (psiClasses.length > 0) {
            return getObjectConfigDO(psiClasses[0], clazzName, clazzParam);
        }
        return null;
    }

    protected void weavingSetGetCode(ActionContext context, ObjectConfigDO setObjConfigDO, ObjectConfigDO getObjConfigDO) {
        Application application = ApplicationManager.getApplication();
        application.runWriteAction(() -> {
            StringBuilder blankSpace = new StringBuilder();
            for (int i = 0; i < distance; i++) {
                blankSpace.append(" ");
            }
            int lineNumberCurrent = context.getDocument().getLineNumber(context.getOffset()) + 1;
            StringBuilder insertString = new StringBuilder();
            for (String param : setObjConfigDO.getParamList()) {
                String getMethodName = getObjConfigDO.getParamGetMtdMap().get(param);
                if (getMethodName == null) {
                    getMethodName = setObjConfigDO.getParamGetMtdMap().get(param);
                }
                insertString.append(blankSpace)
                        .append(setObjConfigDO.getClazzParamName())
                        .append(".").append(setObjConfigDO.getParamSetMtdMap().get(param))
                        .append("(")
                        .append(getObjConfigDO.getClazzParamName())
                        .append(".").append(getMethodName).append("());\n");
            }
            int lineStartOffset = context.getDocument().getLineStartOffset(lineNumberCurrent);
            String finalInsertString = insertString.toString();
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                context.getDocument().insertString(lineStartOffset, finalInsertString);
                context.getEditor().getCaretModel().moveToOffset(lineStartOffset + finalInsertString.length() - 1);
                context.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
            });
        });

    }

    protected List<PsiClass> getPsiClassLinkList(PsiClass psiClass) {
        List<PsiClass> psiClassList = new ArrayList<>();
        PsiClass currentClass = psiClass;
        while (null != currentClass && !"Object".equals(currentClass.getName())) {
            psiClassList.add(currentClass);
            currentClass = currentClass.getSuperClass();
        }
        Collections.reverse(psiClassList);
        return psiClassList;
    }

}
