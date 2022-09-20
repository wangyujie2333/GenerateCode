package com.idea.plugin.orm.support.enums;

import java.util.Arrays;
import java.util.List;

public enum FileCreateTypeEnum {
    DO(Arrays.asList(FileTypePathEnum.DO, FileTypePathEnum.VO)),
    DAO(Arrays.asList(FileTypePathEnum.DAO, FileTypePathEnum.MAPPER)),
    SERVICE(Arrays.asList(FileTypePathEnum.ISERVICE, FileTypePathEnum.SERVICE)),
    CONTROLLER(Arrays.asList(FileTypePathEnum.CONTROLLER)),
    ;
    private List<FileTypePathEnum> fileTypePathList;

    public List<FileTypePathEnum> getFileTypePathList() {
        return fileTypePathList;
    }


    FileCreateTypeEnum(List<FileTypePathEnum> fileTypePathList) {
        this.fileTypePathList = fileTypePathList;
    }
}
