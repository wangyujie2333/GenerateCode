package com.idea.plugin.setting.template;


import java.util.Map;

public class TemplateTaskVO {
    private String taskName;
    private String filePath;
    private Map<String, String> templatePath;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, String> getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(Map<String, String> templatePath) {
        this.templatePath = templatePath;
    }

}
