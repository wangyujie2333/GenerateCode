package com.idea.plugin.report.support;

import com.idea.plugin.utils.DateUtils;

import java.time.LocalDateTime;

public class DayModeule extends ReportModeule {


    public DayModeule(ReportContext context) {
        super(context);
    }

    public String day() {
        return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD);
    }

}
