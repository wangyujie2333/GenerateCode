package com.idea.plugin.report.support;


import com.idea.plugin.setting.support.ReportConfigVO;

public class ReportModuleFactory {
    public static void createModule(ReportContext context, ReportConfigVO config) {
        ReportModeule reportModeule = null;
        String templatePath = null;

        switch (context.getType()) {
            case DAY:
                reportModeule = new DayModeule(context);
                templatePath = config.filePath + config.getDayTemplate().getDay();
                break;
            case WEEK:
                reportModeule = new WeekModeule(context);
                templatePath = config.filePath + config.getDayTemplate().getWeek();
                break;
            default:
                break;
        }
        context.templatePath = templatePath;
        context.setReportModeule(reportModeule);
    }
}