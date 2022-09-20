package com.idea.plugin.sql.support.enums;

import java.util.Comparator;
import java.util.List;

public enum FileDDLTypeEnum {
    CREATE("create", 1),
    ALTER("alter", 2),
    INSERT("insert", 3),
    UPDATE("update", 4),
    INITIAL("alter", 5),
    DELETE("delete", 6),
    ;
    private String code;
    private Integer positon;

    FileDDLTypeEnum(String code, Integer positon) {
        this.code = code;
        this.positon = positon;
    }

    public String getCode() {
        return code;
    }

    public Integer getPositon() {
        return positon;
    }

    public static FileDDLTypeEnum getFirstFileType(List<FileDDLTypeEnum> fileDDLTypeEnumList) {
        return fileDDLTypeEnumList.stream().min(Comparator.comparing(FileDDLTypeEnum::getPositon)).orElse(INITIAL);
    }
}
