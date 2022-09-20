package com.idea.plugin.report.support;

import com.idea.plugin.report.support.module.ReportFileInfo;
import com.idea.plugin.utils.DateUtils;

import java.time.LocalDateTime;

public class ReportModeule extends TemplateModule {

    protected ReportContext context;

    public ReportModeule(ReportContext context) {
        this.context = context;
    }

    public ReportFileInfo getFileInfo() {
        return context.getFileInfo();
    }

    public String now() {
        return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD_HH_MM_SS);
    }

    public String need() {
        return String.join("  \n", getFileInfo().getDone().getNeedList());
    }

    public String bug() {
        return String.join("  \n", getFileInfo().getDone().getBugList());
    }

    public String other() {
        return String.join("  \n", getFileInfo().getDone().getOtherList());
    }

    public String todoneed() {
        return String.join("  \n", getFileInfo().getTodo().getNeedList());
    }

    public String todobug() {
        return String.join("  \n", getFileInfo().getTodo().getBugList());
    }

    public String todoother() {
        return String.join("  \n", getFileInfo().getTodo().getOtherList());
    }

    public long needsum() {
        return getFileInfo().getDone().getNeedList().stream().count();
    }

    public long bugsum() {
        return getFileInfo().getDone().getBugList().size();
    }

    public long othersum() {
        return getFileInfo().getDone().getOtherList().stream().count();
    }

    public String dayfile() {
        return context.getDayfile();
    }

    public String todaySpeak() {
        return String.join("  \n", getFileInfo().getTodaySpeak());
    }

}
