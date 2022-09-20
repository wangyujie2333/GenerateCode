package com.idea.plugin.sql.support.enums;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FieldTypeEnum {
    //字符串类型
    VARCHAR("VARCHAR(%s)", "NVARCHAR2(%s)", JDBCType.VARCHAR, String.class),
    TEXT("TEXT", "CLOB", JDBCType.LONGVARCHAR, String.class),
    CHAR("CHAR", "CHAR", JDBCType.CHAR, String.class),
    //整数类,
    INTEGER("DECIMAL(5)", "NUMBER(5)", JDBCType.INTEGER, Integer.class),
    BIGINT("DECIMAL(9)", "NUMBER(9)", JDBCType.BIGINT, Long.class),
    //    SMALLINT("DECIMAL(5)", "NUMBER(5)", JDBCType.SMALLINT, Integer.class),
//    TINYINT("DECIMAL(5)", "NUMBER(5)", JDBCType.TINYINT, Integer.class),
    //浮点类,
    FLOAT("FLOAT", "BINARY_FLOAT", JDBCType.FLOAT, Float.class),
    DOUBLE("DOUBLE", "BINARY_DOUBLE", JDBCType.DOUBLE, Double.class),
    NUMBER("DECIMAL(%s)", "NUMBER(%s)", JDBCType.DECIMAL, BigDecimal.class),
    //其他类,
//    BOOLEAN("", "", JDBCType.BOOLEAN, Boolean.class),
    DATE("DATE", "DATE", JDBCType.DATE, LocalDateTime.class),
    TIME("DATETIME", "DATE", JDBCType.TIME, LocalDateTime.class),
    TIMESTAMP("TIMESTAMP", "TIMESTAMP", JDBCType.TIMESTAMP, LocalDateTime.class),
//    BIT("", "", JDBCType.BIT, boolean.class),
    ;
    private String mtype;
    private String otype;
    private JDBCType jtype;
    private Class<?> jclass;

    FieldTypeEnum(String mtype, String otype, JDBCType jtype, Class<?> jclass) {
        this.mtype = mtype;
        this.otype = otype;
        this.jtype = jtype;
        this.jclass = jclass;
    }

    public static FieldTypeEnum codeToEnum(String code) {
        Optional<FieldTypeEnum> fieldTypeEnumOptional = Arrays.stream(FieldTypeEnum.values()).filter(fieldTypeEnum -> code.startsWith(fieldTypeEnum.name())).findAny();
        if (fieldTypeEnumOptional.isPresent()) {
            return fieldTypeEnumOptional.get();
        }
        throw new RuntimeException(String.format("字段类型%s不存在", code));
    }

    public static String codeGetArgs(String code) {
        Pattern patternCode = Pattern.compile("(?<=\\()[^\\)]+");
        Matcher matcherCode = patternCode.matcher(code);
        if (matcherCode.find()) {
            return matcherCode.group();
        }
        if (VARCHAR.equals(codeToEnum(code))) {
            return "32";
        }
        if (NUMBER.equals(codeToEnum(code))) {
            return "32, 6";
        }
        return null;
    }

    public String getMtype(Object... args) {
        return String.format(mtype, args);
    }

    public String getType(String arg) {
        if (VARCHAR.name().equals(this.name()) || NUMBER.name().equals(this.name())) {
            return this.name() + "(" + arg + ")";
        }
        return this.name();
    }

    public String getOtype(Object... args) {
        return String.format(otype, args);
    }

    public JDBCType getJtype() {
        return jtype;
    }

    public Class<?> getJclass() {
        return jclass;
    }

    public static FieldTypeEnum getFieldTypeBySqlType(int sqlType) {
        return Arrays.stream(FieldTypeEnum.values())
                .filter(fieldTypeEnum -> fieldTypeEnum.jtype.equals(JDBCType.valueOf(sqlType))).findAny().orElse(null);
    }

    public static FieldTypeEnum getFieldTypeByOType(String oType) {
        return Arrays.stream(FieldTypeEnum.values())
                .filter(fieldTypeEnum -> fieldTypeEnum.otype.startsWith(oType)).findAny().orElse(null);
    }

}
