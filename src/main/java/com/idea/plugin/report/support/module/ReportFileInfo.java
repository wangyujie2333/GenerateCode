package com.idea.plugin.report.support.module;


import com.idea.plugin.report.support.enums.ReportTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ReportFileInfo {
    private static final Pattern pattern = Pattern.compile("(?<=\\(assets/)(.*)(?=\\))");

    private String learn;

    private ReportTypeEnum type = ReportTypeEnum.DAY;
    private TaskInfo todo;
    private TaskInfo done;
    private List<String> todaySpeak = new ArrayList<>();

    public ReportFileInfo() {
    }

    public void init(ReportTypeEnum type) {
        this.type = type;
        this.todo = new TaskInfo();
        this.done = new TaskInfo();
        this.todaySpeak = new ArrayList<>();
    }

    public String getLearn() {
        return learn;
    }

    public void setLearn(String learn) {
        this.learn = this.learn + learn + "\n";
    }

    public ReportTypeEnum getType() {
        return type;
    }

    public void setType(String type) {
        ReportTypeEnum reportTypeEnum = ReportTypeEnum.codeToEnum(type);
        if (reportTypeEnum != null) {
            this.type = reportTypeEnum;
        }
    }

    public TaskInfo getTodo() {
        return todo;
    }

    public void setTodo(TaskInfo todo) {
        this.todo = todo;
    }

    public TaskInfo getDone() {
        return done;
    }

    public void setDone(TaskInfo done) {
        this.done = done;
    }

    public List<String> getTodaySpeak() {
        return this.getStrList(this.learn);
    }

    public List<String> getStrList(String str) {
        List<String> strList = new ArrayList<>();
        if (StringUtils.isNotBlank(str)) {
            String[] split = str.split("\n");
            strList.addAll(Arrays.asList(split));
        }
        return strList;
    }
}
