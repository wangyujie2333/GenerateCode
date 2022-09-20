package com.idea.plugin.sql.support;


import com.idea.plugin.sql.support.enums.ProcedureTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneralSqlInfoVO extends GeneralInfoVO<TableSqlInfoVO> {
    public String filePath;
    public String fileName;
    public ProcedureTypeEnum procedureTypeEnum;
    public List<Map<String, String>> newIdCacheMapList = new ArrayList<>();

    public GeneralSqlInfoVO() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ProcedureTypeEnum getProcedureTypeEnum() {
        return procedureTypeEnum;
    }

    public void setProcedureTypeEnum(ProcedureTypeEnum procedureTypeEnum) {
        this.procedureTypeEnum = procedureTypeEnum;
    }

    public List<Map<String, String>> getNewIdCacheMapList() {
        return newIdCacheMapList;
    }

    public void setNewIdCacheMapList(List<Map<String, String>> newIdCacheMapList) {
        this.newIdCacheMapList = newIdCacheMapList;
    }
}
