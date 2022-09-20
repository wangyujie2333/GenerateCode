package com.idea.plugin.report.support;

import com.idea.plugin.report.support.module.DesignInfo;
import com.idea.plugin.report.support.module.DesignNeedInfo;
import com.idea.plugin.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class DesignModule extends TemplateModule {
    protected DesignInfo designInfo;
    protected int number = 1;


    public DesignModule(DesignInfo designInfo) {
        this.designInfo = designInfo;
        this.number = designInfo.getNumber();
    }

    public String month() {
        return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYMM);
    }

    public String author() {
        return designInfo.getAuthor();
    }

    public String module() {
        return designInfo.getModule();
    }

    public String iteration() {
        return designInfo.getIteration();
    }

    public Integer number() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String background() {
        return designInfo.getNeed().stream().map(DesignNeedInfo::getNeed).collect(Collectors.joining("  \n"));
    }
}
