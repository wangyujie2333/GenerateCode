package com.idea.plugin.popup;

import com.idea.plugin.copy.CopyFileService;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

public class CopyBuildFileAction extends BaseAction {

    protected AppActivationListener instance;

    public CopyBuildFileAction() {
        super();
        synchronized (BaseAction.class) {
            if (instance != null) {
                return;
            }
            instance = new AppActivationListener();
            Application app = ApplicationManager.getApplication();
            Disposable disposable = Disposer.newDisposable();
            Disposer.register(app, disposable);
            MessageBusConnection connection = app.getMessageBus().connect(disposable);
            connection.subscribe(ApplicationActivationListener.TOPIC, instance);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            VirtualFile[] virtualFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
            CopyFileService.copyBuildFile(context.getProject(), virtualFiles);
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.getProject() != null);
    }
}
