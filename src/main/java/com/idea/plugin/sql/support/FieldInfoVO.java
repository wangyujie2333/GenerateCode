package com.idea.plugin.sql.support;

import com.idea.plugin.sql.support.enums.FieldTypeEnum;
import com.idea.plugin.sql.support.enums.NullTypeEnum;
import com.idea.plugin.sql.support.enums.PrimaryTypeEnum;
import com.idea.plugin.translator.TranslatorFactroy;
import org.apache.commons.lang3.StringUtils;

public class FieldInfoVO {
    public String columnName;
    public FieldTypeEnum columnType;
    public String columnTypeArgs;
    public String comment;
    public NullTypeEnum nullType = NullTypeEnum.NULL;
    public PrimaryTypeEnum primary = PrimaryTypeEnum.NON_PRIMARY;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public FieldTypeEnum getColumnType() {
        return columnType;
    }

    public void setColumnType(FieldTypeEnum columnType) {
        this.columnType = columnType;
    }

    public String getColumnTypeArgs() {
        return columnTypeArgs;
    }

    public void setColumnTypeArgs(String columnTypeArgs) {
        this.columnTypeArgs = columnTypeArgs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public NullTypeEnum getNullType() {
        return nullType;
    }

    public void setNullType(NullTypeEnum nullType) {
        this.nullType = nullType;
    }

    public PrimaryTypeEnum getPrimary() {
        return primary;
    }

    public void setPrimary(PrimaryTypeEnum primary) {
        this.primary = primary;
    }

    public static FieldInfoVO builder() {
        return new FieldInfoVO();
    }


    public FieldInfoVO columnName(String columnName) {
        this.columnName = columnName;
        if (StringUtils.isEmpty(comment)) {
            this.comment = TranslatorFactroy.translate(columnName);
        }
        return this;
    }

    public FieldInfoVO columnType(FieldTypeEnum columnType) {
        this.columnType = columnType;
        return this;
    }

    public FieldInfoVO columnTypeArgs(String columnTypeArgs) {
        this.columnTypeArgs = columnTypeArgs;
        return this;
    }

    public FieldInfoVO comment(String comment) {
        this.comment = comment;
        return this;
    }

    public FieldInfoVO nullType(NullTypeEnum nullType) {
        this.nullType = nullType;
        return this;
    }

    public FieldInfoVO primary(PrimaryTypeEnum primary) {
        this.primary = primary;
        return this;
    }
}
