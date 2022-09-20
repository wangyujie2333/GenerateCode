package com.idea.plugin.orm.support.enums;

public enum ClazzTypeEnum {
    CLASS_CLAZZ("class"),
    INTERFACE_CLAZZ("interface"),
    ;
    private String code;

    ClazzTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
