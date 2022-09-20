package com.idea.plugin.popup;

import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.utils.NoticeUtil;
import com.idea.plugin.utils.ThreadLocalUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBusConnection;

import java.util.Arrays;
import java.util.Optional;

public class BaseAction extends AnAction {

    protected static ActionContext context;

    /**
     * 行动执行
     *
     * @param e e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        NoticeUtil.init(this.getClass().getSimpleName());
        context = getActionContext(e);
    }

    /**
     * 更新
     *
     * @param e e
     */
    @Override
    public void update(AnActionEvent e) {
        context = getActionContext(e);
        e.getPresentation().setVisible(Optional.ofNullable(context.getPsiElements())
                .map(Arrays::stream)
                .map(s -> s.noneMatch(it -> it instanceof DbTable))
                .orElse(true));
    }

    protected ActionContext getActionContext(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        DataContext dataContext = e.getDataContext();
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        // 基础信息
        PsiElement[] psiElements = new PsiElement[0];
        try {
            psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        VirtualFile[] virtualFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        // 封装生成对象上下文
        ActionContext actionContext = new ActionContext();
        ActionContext.globalproject = project;
        actionContext.setProject(project);
        actionContext.setPsiFile(psiFile);
        actionContext.setVirtualFiles(virtualFiles);
        actionContext.setDataContext(dataContext);
        actionContext.setEditor(editor);
        actionContext.setPsiElement(psiElement);
        actionContext.setPsiElements(psiElements);
        if (editor != null) {
            Document document = editor.getDocument();
            actionContext.setOffset(editor.getCaretModel().getOffset());
            actionContext.setDocument(document);
            actionContext.setLineNumber(document.getLineNumber(actionContext.getOffset()));
            actionContext.setStartOffset(document.getLineStartOffset(actionContext.getLineNumber()));
            actionContext.setEditorText(document.getText());
        }
        if (psiFile != null && psiFile.getVirtualFile() != null) {
            actionContext.setPsiFilePath(psiFile.getVirtualFile().getPath());
        }
        ThreadLocalUtils.set(ActionContext.class, "ActionContext", context);
        return actionContext;
    }
}
