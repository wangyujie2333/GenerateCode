package com.idea.plugin.popup;

import com.idea.plugin.report.service.ReportGenerator;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.JsonUtil;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class InitAction extends BaseAction {


    private static final ReportGenerator reportGenerator = new ReportGenerator();

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);
        reportGenerator.initReportConfig(context);
    }



    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.isPsiFilePathSuffix("param.json"));
    }

}
