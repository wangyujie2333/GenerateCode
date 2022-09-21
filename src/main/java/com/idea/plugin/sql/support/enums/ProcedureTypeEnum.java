package com.idea.plugin.sql.support.enums;

import com.idea.plugin.orm.support.enums.FileCreateTypeEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ProcedureTypeEnum {
    INITIAL("", FileDDLTypeEnum.INITIAL, Collections.emptyList()),
    ADD_TABLE("新增内置表", FileDDLTypeEnum.CREATE, Arrays.asList(
            "tableName",
            "fieldInfos")),
    ADD_INDEX("新增内置索引", FileDDLTypeEnum.ALTER, Arrays.asList(
            "tableName",
            "indexInfos")),
    ADD_COLUMN("新增内置列", FileDDLTypeEnum.ALTER, Arrays.asList(
            "tableName",
            "fieldInfos")),
    MODIFY_COLUMN("修改内置列", FileDDLTypeEnum.ALTER, Arrays.asList(
            "tableName",
            "fieldInfos")),
    ADD_DATA("新增内置数据", FileDDLTypeEnum.INSERT, Arrays.asList(
            "tableName",
            "insertColumnName",
            "insertColumnParam")),
    INSERT_DATA("新增内置数据", FileDDLTypeEnum.INSERT, Arrays.asList(
            "tableName",
            "insertData")),
    INSERT_SQL("新增内置数据", FileDDLTypeEnum.INSERT, Arrays.asList(
            "jdbcUrl",
            "username",
            "dbpasswd",
            "tableName",
            "insertSql")),
    DO(FileCreateTypeEnum.DO, Arrays.asList("modulePath")),
    DAO(FileCreateTypeEnum.DAO, Arrays.asList("modulePath")),
    SERVICE(FileCreateTypeEnum.SERVICE, Arrays.asList("modulePath")),
    CONTROLLER(FileCreateTypeEnum.CONTROLLER, Arrays.asList("modulePath")),
    ;
    private String name;
    private FileDDLTypeEnum fileType;
    private FileCreateTypeEnum fileCreateType;
    private List<String> mustFieldList;

    ProcedureTypeEnum(String name, FileDDLTypeEnum fileType, List<String> mustFieldList) {
        this.name = name;
        this.fileType = fileType;
        this.mustFieldList = mustFieldList;
    }

    ProcedureTypeEnum(FileCreateTypeEnum fileCreateType, List<String> mustFieldList) {
        this.fileCreateType = fileCreateType;
        this.mustFieldList = mustFieldList;
    }

    ProcedureTypeEnum(String name, FileDDLTypeEnum fileType, FileCreateTypeEnum fileCreateType, List<String> mustFieldList) {
        this.name = name;
        this.fileType = fileType;
        this.fileCreateType = fileCreateType;
        this.mustFieldList = mustFieldList;
    }

    public String getName() {
        return name;
    }

    public FileDDLTypeEnum getFileType() {
        return fileType;
    }

    public FileCreateTypeEnum getFileCreateType() {
        return fileCreateType;
    }

    public List<String> getMustFieldList() {
        return mustFieldList;
    }

    public static ProcedureTypeEnum codeToEnum(String code) {
        return Arrays.stream(ProcedureTypeEnum.values()).filter(procedureTypeEnum -> procedureTypeEnum.name().equals(code.toUpperCase())).findAny().orElse(null);
    }

}
