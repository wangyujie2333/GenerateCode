package com.idea.plugin.popup;

import com.idea.plugin.log.LogGenerateService;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;


public class JavaLogAction extends BaseAction {
    private static final LogGenerateService logGenerateService = new LogGenerateService();

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            logGenerateService.doGenerate(context);
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(Optional.ofNullable(context.getVirtualFiles())
                .map(Arrays::stream)
                .map(s -> s.anyMatch(it -> it.getPath().endsWith(("java")) || it.isDirectory()))
                .orElse(false));
    }

}
