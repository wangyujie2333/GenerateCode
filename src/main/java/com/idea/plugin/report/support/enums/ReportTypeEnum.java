package com.idea.plugin.report.support.enums;

import java.util.Arrays;

public enum ReportTypeEnum {
    DAY,
    WEEK,
    ;

    public static ReportTypeEnum codeToEnum(String code) {
        return Arrays.stream(ReportTypeEnum.values()).filter(fileTypeEnum -> fileTypeEnum.name().equals(code.toUpperCase())).findAny().orElse(null);
    }
}
