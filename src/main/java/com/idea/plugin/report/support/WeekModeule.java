package com.idea.plugin.report.support;

import com.idea.plugin.report.support.enums.ReportTypeEnum;
import com.idea.plugin.report.support.module.ReportFileInfo;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.utils.ActionUtils;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class WeekModeule extends ReportModeule {
    public WeekModeule(ReportContext context) {
        super(context);
        ReportConfigVO config = ToolSettings.getReportConfig();
        context.getFileInfo().init(ReportTypeEnum.WEEK);
        LocalDateTime now = LocalDateTime.now();
        Calendar calendar = DateUtils.DateToCalendar(LocalDateTime.now());
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        //下周一
        LocalDateTime nextDateTime = now.plusDays(9 - i);
        String nextStr = DateUtils.LocalDateTimeToStr(nextDateTime, DateUtils.YYYY_MM_DD);
        //星期一到星期五
        List<String> dayfiles = new ArrayList<>();
        for (int j = 2; j < 7; j++) {
            int plusDays = j - i;
            LocalDateTime localDateTime = now.plusDays(plusDays);
            String todayStr = DateUtils.LocalDateTimeToStr(localDateTime, DateUtils.YYYY_MM_DD);
            //[2022-05-23](/day/2022-05-23.md)
            String filePath = config.filePath + config.dayTemplate.getDayPath() + "/" + todayStr + ".md";
            List<String> dayStrlist = FileUtils.readFile(filePath);
            if (CollectionUtils.isEmpty(dayStrlist)) {
                continue;
            }
            dayfiles.add("[" + todayStr + "](" + config.dayTemplate.getDayPath() + "/" + todayStr + ".md)");
            ReportFileInfo reportFileInfo = ActionUtils.readReportFileInfo(dayStrlist);
            if (reportFileInfo == null) {
                return;
            }
            reportFileInfo.getDone().getNeedList().forEach(s -> context.getFileInfo().getDone().setNeed(s.trim() + "-" + todayStr));
            reportFileInfo.getDone().getBugList().forEach(s -> context.getFileInfo().getDone().setBug(s.trim() + "-" + todayStr));
            reportFileInfo.getDone().getOtherList().forEach(s -> context.getFileInfo().getDone().setOther(s.trim() + "-" + todayStr));
            if (plusDays == 0) {
                reportFileInfo.getTodo().getNeedList().forEach(s -> context.getFileInfo().getTodo().setNeed(s.trim() + "-" + nextStr));
                reportFileInfo.getTodo().getBugList().forEach(s -> context.getFileInfo().getTodo().setBug(s.trim() + "-" + nextStr));
                reportFileInfo.getTodo().getOtherList().forEach(s -> context.getFileInfo().getTodo().setOther(s.trim() + "-" + nextStr));
            }
            if (CollectionUtils.isNotEmpty(reportFileInfo.getTodaySpeak())) {
                String todaySpeak = String.join("", reportFileInfo.getTodaySpeak()).trim();
                if (StringUtils.isNotEmpty(todaySpeak)) {
                    context.getFileInfo().setLearn("### " + todayStr);
                    reportFileInfo.getTodaySpeak().forEach(s -> context.getFileInfo().setLearn(s));
                }
            }
        }
        context.setDayfile(String.join("  \n", dayfiles));
    }

    public String week() {
        LocalDateTime now = LocalDateTime.now();
        Calendar calendar = DateUtils.DateToCalendar(now);
        int weekNumbe = calendar.get(Calendar.WEEK_OF_YEAR);
        return now.getYear() + "-" + weekNumbe + "Week";
    }


    public List<String> allbugsum() {
        return context.getFileInfo().getDone().getBugList().stream().filter(bugStr -> !bugStr.contains("-转") && !bugStr.contains("-hotfix")).collect(Collectors.toList());
    }

    public long bugsum() {
        return allbugsum().size();
    }

    public Long projectbugsum() {
        return allbugsum().stream().filter(bugStr -> bugStr.contains("-项目")).count();
    }

    public Long productbugsum() {
        return allbugsum().stream().filter(bugStr -> !bugStr.contains("-项目")).count();
    }

    public Long invalidbugsum() {
        return allbugsum().stream().filter(bugStr -> bugStr.contains("-无效")).count();
    }

    public Long projectinvalidbugsum() {
        return allbugsum().stream().filter(bugStr -> bugStr.contains("-项目") && bugStr.contains("-无效")).count();
    }

    public Long productinvalidbugsum() {
        return allbugsum().stream().filter(bugStr -> !bugStr.contains("-项目") && bugStr.contains("-无效")).count();
    }

    public Long repeatbugsum() {
        return allbugsum().stream().filter(bugStr -> bugStr.contains("-重复")).count();
    }

    public Long projectrepeatbugsum() {
        return allbugsum().stream().filter(bugStr -> bugStr.contains("-项目") && bugStr.contains("-重复")).count();
    }

    public Long productrepeatbugsum() {
        return allbugsum().stream().filter(bugStr -> !bugStr.contains("-项目") && bugStr.contains("-重复")).count();
    }

    public Integer remainingbugsum() {
        return context.getFileInfo().getTodo().getBugList().size();
    }

    public Long projectremainingbugsum() {
        return context.getFileInfo().getTodo().getBugList().stream().filter(bugStr -> bugStr.contains("-项目")).count();
    }

    public Long producremainingbugsum() {
        return context.getFileInfo().getTodo().getBugList().stream().filter(bugStr -> !bugStr.contains("-项目")).count();
    }

}
