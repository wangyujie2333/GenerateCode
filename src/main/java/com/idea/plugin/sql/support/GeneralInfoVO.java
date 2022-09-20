package com.idea.plugin.sql.support;


import com.idea.plugin.setting.support.BaseConfigVO;
import com.idea.plugin.sql.support.enums.DataTypeEnum;
import org.apache.commons.collections.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneralInfoVO<T extends TableInfoVO> extends BaseConfigVO {

    public String author;
    public String date;
    public String jdbcUrl;
    public String username;
    public String dbpasswd;
    public String schema;
    public DataTypeEnum dataType;

    public List<T> tableInfos = new ArrayList<>();

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
        return dateFormat.format(localDateTime);
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public DataTypeEnum getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeEnum dataType) {
        this.dataType = dataType;
    }

    public List<T> getTableInfos() {
        return tableInfos.stream().filter(tableInfo -> CollectionUtils.isNotEmpty(tableInfo.getProcedureTypeList())).collect(Collectors.toList());
    }

    public void setTableInfos(List<T> tableInfos) {
        this.tableInfos = tableInfos;
    }
}
