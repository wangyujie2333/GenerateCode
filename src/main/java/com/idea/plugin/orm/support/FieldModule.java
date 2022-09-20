package com.idea.plugin.orm.support;

import com.google.common.base.CaseFormat;
import com.idea.plugin.sql.support.FieldInfoVO;
import com.idea.plugin.sql.support.enums.PrimaryTypeEnum;
import org.apache.commons.lang3.StringUtils;

public class FieldModule {

    private FieldInfoVO fieldInfoVO;

    public FieldModule(FieldInfoVO fieldInfoVO) {
        this.fieldInfoVO = fieldInfoVO;
    }

    public String getComment() {
        return fieldInfoVO.comment;
    }

    public String getClassSimpleName() {
        return fieldInfoVO.columnType.getJclass().getSimpleName();
    }

    public String getColumnName() {
        return fieldInfoVO.columnName;
    }

    public String getName() {
        String str = fieldInfoVO.columnName;
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

    public String getClassName() {
        return fieldInfoVO.columnType.getJclass().getName();
    }

    public String getJdbcType() {
        return fieldInfoVO.columnType.getJtype().getName();
    }

    public String getJavaType() {
        Class<?> jclass = fieldInfoVO.columnType.getJclass();
        String className = getClassName();
        if (!jclass.isPrimitive() && !"java.lang".equals(StringUtils.substringBeforeLast(className, "."))) {
            return " javaType=\"" + className + "\"";
        }
        return "";
    }

    public boolean isId() {
        return PrimaryTypeEnum.PRIMARY.equals(fieldInfoVO.primary);
    }

    public boolean isImport() {
        String className = getClassName();
        return !fieldInfoVO.columnType.getJclass().isPrimitive() && !"java.lang".equals(StringUtils.substringBeforeLast(className, "."));
    }

}
