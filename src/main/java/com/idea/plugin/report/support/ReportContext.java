package com.idea.plugin.report.support;

import com.idea.plugin.report.support.enums.ReportTypeEnum;
import com.idea.plugin.report.support.module.ReportFileInfo;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.FileUtils;
import com.intellij.openapi.vfs.VirtualFile;

import java.time.LocalDateTime;
import java.util.Calendar;

public class ReportContext {
    public ReportTypeEnum type;
    public String templatePath;
    public String fileName;
    public ReportModeule reportModeule;
    public ReportFileInfo fileInfo;
    public String dayfile;
    public String absulotePath;
    public ReportConfigVO config;

    public ReportContext(ReportTypeEnum type, ReportConfigVO config) {
        this.type = type;
        this.config = config;
        VirtualFile packageDir = FileUtils.createDir(config.filePath + config.getMdpath(this.getType()));
        this.absulotePath = packageDir.getPath() + "/" + this.getFileName() + ".md";
    }

    public ReportContext(ReportTypeEnum type) {
        this.type = type;
    }

    public ReportTypeEnum getType() {
        return type;
    }

    public void setType(ReportTypeEnum type) {
        this.type = type;
    }

    public String getFileName() {
        if (ReportTypeEnum.DAY.equals(type)) {
            return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD);
        } else if (ReportTypeEnum.WEEK.equals(type)) {
            LocalDateTime now = LocalDateTime.now();
            Calendar calendar = DateUtils.DateToCalendar(LocalDateTime.now());
            int weekNumbe = calendar.get(Calendar.WEEK_OF_YEAR);
            return now.getYear() + "-" + weekNumbe + "Week";
        }
        return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ReportModeule getReportModeule() {
        return reportModeule;
    }

    public void setReportModeule(ReportModeule reportModeule) {
        this.reportModeule = reportModeule;
    }

    public ReportFileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(ReportFileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getDayfile() {
        return dayfile;
    }

    public void setDayfile(String dayfile) {
        this.dayfile = dayfile;
    }

    public ReportConfigVO getConfig() {
        return config;
    }

    public void setConfig(ReportConfigVO config) {
        this.config = config;
    }
}
