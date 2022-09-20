package com.idea.plugin.sql.support.enums;

public enum DataTypeEnum {
    MYSQL("mysql"),
    ORACLE("oracle"),
    ;
    private String code;

    DataTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
