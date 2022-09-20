package com.idea.plugin.sql.support;


import com.idea.plugin.setting.support.BaseConfigVO;
import com.idea.plugin.sql.support.enums.ProcedureTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TableInfoVO extends BaseConfigVO {

    public String procedureType;
    public String comment;
    public String tableName;
    public String tableComment;
    public String insertColumnName;
    public String insertColumnParam;
    public String insertSql;
    public List<String> insertData = new ArrayList<>();

    public List<FieldInfoVO> fieldInfos = new ArrayList<>();
    public List<IndexInfoVO> indexInfos = new ArrayList<>();

    public List<ProcedureTypeEnum> procedureTypeList = new ArrayList<>();


    public String tableShortName() {
        StringBuilder tableShortName = new StringBuilder();
        String[] nameArr = tableName.split("_");
        for (int i = 0; i < nameArr.length; i++) {
            if (nameArr.length > 1 && i < nameArr.length - 1) {
                tableShortName.append(nameArr[i].charAt(0));
            } else {
                tableShortName.append(nameArr[i]);
            }
        }
        return tableShortName.toString();
    }

    public String getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(String procedureType) {
        this.procedureType = procedureType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getInsertColumnName() {
        return insertColumnName;
    }

    public void setInsertColumnName(String insertColumnName) {
        this.insertColumnName = insertColumnName;
    }

    public String getInsertColumnParam() {
        return insertColumnParam;
    }

    public void setInsertColumnParam(String insertColumnParam) {
        this.insertColumnParam = insertColumnParam;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }

    public List<String> getInsertData() {
        return insertData;
    }

    public void setInsertData(List<String> insertData) {
        this.insertData = insertData;
    }

    public void addInsertData(String insertData) {
        this.insertData.add(insertData);
    }

    public List<FieldInfoVO> getFieldInfos() {
        if (CollectionUtils.isEmpty(fieldInfos)) {
            return Collections.emptyList();
        }
        return fieldInfos.stream().filter(fieldInfo -> StringUtils.isNotEmpty(fieldInfo.getColumnName())).collect(Collectors.toList());
    }

    public void setFieldInfos(List<FieldInfoVO> fieldInfos) {
        this.fieldInfos = fieldInfos;
    }

    public void addFieldInfos(FieldInfoVO fieldInfoVO) {
        this.fieldInfos.add(fieldInfoVO);
    }

    public List<IndexInfoVO> getIndexInfos() {
        if (CollectionUtils.isEmpty(indexInfos)) {
            return Collections.emptyList();
        }
        return indexInfos.stream().filter(indexInfo -> StringUtils.isNotEmpty(indexInfo.getIndexName())).collect(Collectors.toList());
    }

    public void setIndexInfos(List<IndexInfoVO> indexInfos) {
        this.indexInfos = indexInfos;
    }

    public void addIndexInfos(IndexInfoVO indexInfo) {
        this.indexInfos.add(indexInfo);
    }

    public List<ProcedureTypeEnum> getProcedureTypeList() {
        if (StringUtils.isEmpty(procedureType)) {
            return Collections.emptyList();
        }
        return Arrays.stream(procedureType.split(",")).map(code -> ProcedureTypeEnum.codeToEnum(code.trim())).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void setProcedureTypeList(List<ProcedureTypeEnum> procedureTypeList) {
        this.procedureTypeList = procedureTypeList;
    }
}
