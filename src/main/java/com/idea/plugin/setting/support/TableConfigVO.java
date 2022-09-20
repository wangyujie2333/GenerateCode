package com.idea.plugin.setting.support;

import com.idea.plugin.sql.support.FieldInfoVO;
import com.idea.plugin.sql.support.TableInfoVO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TableConfigVO extends BaseConfigVO {
    public String author;
    public String filePath;
    public String fileName;
    public String jdbcUrl;
    public String username;
    public String dbpasswd;
    public String tableName;
    public Map<String, List<String>> tabNameCacheMap = new ConcurrentHashMap<>();
    public Map<String, TableInfoVO> tableInfoCacheMap = new ConcurrentHashMap<>();
    public Map<String, List<FieldInfoVO>> fieldInfoCacheMap = new ConcurrentHashMap<>();
    public List<String> procedureTypeList = new ArrayList<>();
    public List<TableInfoVO> tableInfoVOS = new ArrayList<>();

    public List<String> getTableNameList() {
        if (StringUtils.isNotEmpty(tableName)) {
            return Arrays.stream(tableName.split(";")).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDbpasswd() {
        return dbpasswd;
    }

    public void setDbpasswd(String dbpasswd) {
        this.dbpasswd = dbpasswd;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, List<String>> getTabNameCacheMap() {
        return tabNameCacheMap;
    }

    public void setTabNameCacheMap(Map<String, List<String>> tabNameCacheMap) {
        this.tabNameCacheMap = tabNameCacheMap;
    }

    public Map<String, TableInfoVO> getTableInfoCacheMap() {
        return tableInfoCacheMap;
    }

    public void setTableInfoCacheMap(Map<String, TableInfoVO> tableInfoCacheMap) {
        this.tableInfoCacheMap = tableInfoCacheMap;
    }

    public Map<String, List<FieldInfoVO>> getFieldInfoCacheMap() {
        return fieldInfoCacheMap;
    }

    public void setFieldInfoCacheMap(Map<String, List<FieldInfoVO>> fieldInfoCacheMap) {
        this.fieldInfoCacheMap = fieldInfoCacheMap;
    }

    public List<String> getProcedureTypeList() {
        return procedureTypeList;
    }

    public void setProcedureTypeList(List<String> procedureTypeList) {
        this.procedureTypeList = procedureTypeList;
    }

    public List<TableInfoVO> getTableInfoVOS() {
        return tableInfoVOS;
    }

    public void setTableInfoVOS(List<TableInfoVO> tableInfoVOS) {
        this.tableInfoVOS = tableInfoVOS;
    }
}
