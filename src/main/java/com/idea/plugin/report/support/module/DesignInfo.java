package com.idea.plugin.report.support.module;

import java.util.List;

public class DesignInfo {
    private String author;
    private String module;
    private Integer number = 0;
    private String iteration;
    private List<DesignNeedInfo> need;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<DesignNeedInfo> getNeed() {
        return need;
    }

    public void setNeed(List<DesignNeedInfo> need) {
        this.need = need;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }
}
