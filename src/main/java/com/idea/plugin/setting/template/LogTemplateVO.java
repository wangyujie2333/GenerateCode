package com.idea.plugin.setting.template;


public class LogTemplateVO {
    private String serial;
    private Boolean inner;
    private String logCommon;
    private String logCommonMsg;
    private String logEnter;
    private String logEnterMsg;
    private String logLeave;
    private String logLeaverMsg;
    private String logError;

    public LogTemplateVO() {
    }

    public void setInner(Boolean inner) {
        this.inner = inner;
    }

    public Boolean getInner() {
        return inner;
    }

    public void setLogEnter(String logEnter) {
        this.logEnter = logEnter;
    }

    public String getLogCommon() {
        return logCommon;
    }

    public void setLogCommon(String logCommon) {
        this.logCommon = logCommon;
    }

    public String getLogCommonMsg() {
        return logCommonMsg;
    }

    public void setLogCommonMsg(String logCommonMsg) {
        this.logCommonMsg = logCommonMsg;
    }

    public String getLogEnter() {
        return logEnter;
    }

    public void setLogEnterMsg(String logEnterMsg) {
        this.logEnterMsg = logEnterMsg;
    }

    public String getLogEnterMsg() {
        return logEnterMsg;
    }

    public void setLogLeave(String logLeave) {
        this.logLeave = logLeave;
    }

    public String getLogLeave() {
        return logLeave;
    }

    public void setLogLeaverMsg(String logLeaverMsg) {
        this.logLeaverMsg = logLeaverMsg;
    }

    public String getLogLeaverMsg() {
        return logLeaverMsg;
    }

    public void setLogError(String logError) {
        this.logError = logError;
    }

    public String getLogError() {
        return logError;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }
}
