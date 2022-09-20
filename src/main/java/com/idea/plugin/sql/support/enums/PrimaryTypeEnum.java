package com.idea.plugin.sql.support.enums;

import java.util.Arrays;

public enum PrimaryTypeEnum {
    PRIMARY(" PRIMARY KEY"),
    NON_PRIMARY(""),
    ;
    private String code;

    PrimaryTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PrimaryTypeEnum codeToEnum(String code) {
        return Arrays.stream(PrimaryTypeEnum.values()).filter(primaryTypeEnum -> primaryTypeEnum.name().equals(code)).findAny().orElse(null);
    }
}
