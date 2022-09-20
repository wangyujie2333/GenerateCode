package com.idea.plugin.charreplace.support;

import java.util.Arrays;


public enum SupportFileTypeEnum {
    JAVA("java", new String[]{"//", "/*"}, new String[]{"*/"}),
    SQL("sql", new String[]{"--", "/*"}, new String[]{"*/"}),
    XML("xml", new String[]{"<!--"}, new String[]{"-->"}),
    YML("yml", new String[]{"#"}, new String[]{"#"}),
    PROPERTIES("properties", new String[]{"#"}, new String[]{"#"}),
    JSON("json", new String[]{}, new String[]{}),
    ;


    private final String code;
    private final String[] commentStart;
    private final String[] commentEnd;

    SupportFileTypeEnum(String code, String[] commentStart, String[] commentEnd) {
        this.code = code;
        this.commentStart = commentStart;
        this.commentEnd = commentEnd;
    }

    public static SupportFileTypeEnum codeToEnum(String code) {
        return Arrays.stream(SupportFileTypeEnum.values()).filter(fileTypeEnum -> fileTypeEnum.getCode().equals(code.toLowerCase())).findAny().orElse(null);

    }

    public String getCode() {
        return code;
    }

    public String[] getCommentStart() {
        return commentStart;
    }

    public String[] getCommentEnd() {
        return commentEnd;
    }
}
