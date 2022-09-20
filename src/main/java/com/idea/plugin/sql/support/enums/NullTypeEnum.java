package com.idea.plugin.sql.support.enums;

import java.util.Arrays;

public enum NullTypeEnum {
    NULL(""),
    NOT_NULL(" NOT NULL"),
    ;
    private String code;

    NullTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static NullTypeEnum codeToEnum(String code) {
        return Arrays.stream(NullTypeEnum.values()).filter(nullTypeEnum -> nullTypeEnum.name().equals(code)).findAny().orElse(null);
    }
}
