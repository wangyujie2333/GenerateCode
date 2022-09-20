package com.idea.plugin.report.support;

import com.idea.plugin.api.WanNianLiVO;
import com.idea.plugin.utils.DateUtils;

import java.time.LocalDateTime;

public class DayModeule extends ReportModeule {


    public DayModeule(ReportContext context) {
        super(context);
    }

    public String day() {
        return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD);
    }

    public String suit() {
        return getData().getSuit();
    }

    public WanNianLiVO getWanNianLiVO() {
        if (context.getConfig() == null && context.getConfig().getWanNianLiVO() == null) {
            return new WanNianLiVO();
        }
        return context.getConfig().getWanNianLiVO();
    }

    public WanNianLiVO.Data getData() {
        if (getWanNianLiVO().getData() == null) {
            return new WanNianLiVO.Data();
        }
        return getWanNianLiVO().getData();
    }

    public String avoid() {
        return getData().getAvoid();
    }

    public String lunarCalendar() {
        return getData().getLunarCalendar();
    }

    public String dayOfYear() {
        return String.valueOf(getData().getDayOfYear());
    }

    public String weekOfYear() {
        return String.valueOf(getData().getWeekOfYear());
    }

    public String solarTerms() {
        return getData().getSolarTerms();
    }
}
