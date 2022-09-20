package com.idea.plugin.setting.template;


public enum TemplateTaskPathEnum {
    HEAD("head"),
    BODY("body"),
    TAIL("tail"),
    ;
    private String code;

    TemplateTaskPathEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
