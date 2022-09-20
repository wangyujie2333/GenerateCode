package com.idea.plugin.popup;

import com.idea.plugin.report.service.ReportGenerator;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.JsonUtil;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ReportAction extends BaseAction {


    private static final ReportGenerator reportGenerator = new ReportGenerator();

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);
        try {
            ReportConfigVO reportConfigVO = JsonUtil.fromJson(context.getEditorText(), ReportConfigVO.class);
            if (reportConfigVO != null) {
                archiveFile(reportConfigVO);
                ReportConfigVO config = ToolSettings.getReportConfig();
                config.copy(reportConfigVO);
                reportGenerator.runSchedule();
                NoticeUtil.info("日志启动成功");
            }
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    private void archiveFile(ReportConfigVO reportConfigVO) {
        if (reportConfigVO.archive != null && reportConfigVO.archive.getIsarchive()) {
            reportConfigVO.archive.getArchiveFilePath().forEach(filePath -> {
                reportGenerator.backupFile(reportConfigVO.filePath + filePath, reportConfigVO.filePath + reportConfigVO.archive.getArchivePath() + filePath);
                FileUtils.delete(reportConfigVO.filePath + filePath);
            });
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.isPsiFilePathSuffix("param.json"));
    }

}
