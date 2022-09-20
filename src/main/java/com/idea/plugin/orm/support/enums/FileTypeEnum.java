package com.idea.plugin.orm.support.enums;

import java.util.Arrays;

public enum FileTypeEnum {
    JAVA("java", "src/main/java/"),
    XML("xml", "src/main/resources/"),
    ;
    String type;
    String path;

    FileTypeEnum(String type, String path) {
        this.type = type;
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public static FileTypeEnum codeToEnum(String code) {
        return Arrays.stream(FileTypeEnum.values()).filter(fileTypeEnum -> fileTypeEnum.getType().equals(code.toLowerCase())).findAny().orElse(null);
    }
}
