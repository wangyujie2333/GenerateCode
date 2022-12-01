package com.idea.plugin.popup;

import com.idea.plugin.method.DataGenerateService;
import com.idea.plugin.orm.support.enums.FileTypeEnum;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class GenerateAllDataAction extends BaseAction {

    private static final DataGenerateService dataGenerateService = new DataGenerateService();

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            dataGenerateService.doGenerate(context);
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.isPsiFilePathSuffix(FileTypeEnum.JAVA.getType()));
    }
}
