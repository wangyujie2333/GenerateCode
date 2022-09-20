package com.idea.plugin.document.support;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public enum JavaTypeEnum {

    VOID_TYPE(Object.class, "void"),
    BYTE_TYPE(Byte.class, JDBCType.INTEGER, "byte"),
    SHORT_TYPE(Short.class, JDBCType.INTEGER, "short"),
    INT_TYPE(Integer.class, JDBCType.INTEGER, "int"),
    LONG_TYPE(Long.class, JDBCType.BIGINT, "long"),
    CHAR_TYPE(Character.class, JDBCType.CHAR, "char"),
    FLOAT_TYPE(Float.class, JDBCType.FLOAT, "float"),
    DOUBLE_TYPE(Double.class, JDBCType.DOUBLE, "double"),
    BOOLEAN_TYPE(Boolean.class, JDBCType.BOOLEAN, "boolean"),
    OBJECT_TYPE(Object.class, JDBCType.VARCHAR),
    STRING_TYPE(String.class, JDBCType.VARCHAR),
    BIGDECIMAL_TYPE(BigDecimal.class, JDBCType.VARCHAR),
    TIMESTAMP_TYPE(Timestamp.class, JDBCType.TIMESTAMP),
    LOCAL_DATE_TYPE(LocalDate.class, JDBCType.TIMESTAMP),
    LOCAL_DATE_TIME_TYPE(LocalDateTime.class, JDBCType.TIMESTAMP),
    DATE_TYPE(Date.class, JDBCType.TIMESTAMP),
    LIST_TYPE(List.class, JDBCType.VARCHAR),
    MAP_TYPE(Map.class, JDBCType.VARCHAR),
    ;

    private Class<?> calzz;
    private JDBCType jdbcType;
    private String basicCode;

    JavaTypeEnum(Class<?> calzz) {
        this.calzz = calzz;
    }

    JavaTypeEnum(Class<?> calzz, String basicCode) {
        this.calzz = calzz;
        this.basicCode = basicCode;
    }

    JavaTypeEnum(Class<?> calzz, JDBCType jdbcType) {
        this.calzz = calzz;
        this.jdbcType = jdbcType;
    }

    JavaTypeEnum(Class<?> calzz, JDBCType jdbcType, String basicCode) {
        this.calzz = calzz;
        this.jdbcType = jdbcType;
        this.basicCode = basicCode;
    }

    public static JavaTypeEnum clazzToEnum(Object obj) {
        if (obj == null) {
            return OBJECT_TYPE;
        } else if (obj instanceof List) {
            return LIST_TYPE;
        } else if (obj instanceof Map) {
            return MAP_TYPE;
        } else {
            return Arrays.stream(JavaTypeEnum.values()).filter(javaTypeEnum ->
                    javaTypeEnum.getCalzz().equals(obj.getClass())).findAny().orElse(OBJECT_TYPE);
        }
    }

    public static JavaTypeEnum codeToEnum(String clazzName) {
        if (clazzName.startsWith(JavaTypeEnum.LIST_TYPE.getCalzz().getName())
                || clazzName.startsWith(JavaTypeEnum.LIST_TYPE.getCalzz().getSimpleName())) {
            return JavaTypeEnum.LIST_TYPE;
        }
        if (clazzName.startsWith(JavaTypeEnum.MAP_TYPE.getCalzz().getName())
                || clazzName.startsWith(JavaTypeEnum.MAP_TYPE.getCalzz().getSimpleName())) {
            return JavaTypeEnum.MAP_TYPE;
        }
        String simpClazzName = clazzName.substring(clazzName.lastIndexOf(".") + 1);
        if (isBasic(simpClazzName)) {
            return Arrays.stream(JavaTypeEnum.values()).filter(javaTypeEnum ->
                    simpClazzName.equals(javaTypeEnum.getBasicName())).findAny().get();
        } else {
            return Arrays.stream(JavaTypeEnum.values()).filter(javaTypeEnum ->
                    StringUtils.isEmpty(javaTypeEnum.getBasicName())
                            && javaTypeEnum.getCalzz().getSimpleName().equalsIgnoreCase(simpClazzName)).findAny().orElse(null);
        }
    }

    public static boolean isBasic(String type) {
        return type != null && Arrays.stream(JavaTypeEnum.values()).anyMatch(javaTypeEnum -> {
            if (type.equals(javaTypeEnum.getBasicName())) {
                return true;
            }
            return false;
        });
    }

    public boolean isImport() {
        return this.equals(BIGDECIMAL_TYPE)
                || this.equals(DATE_TYPE)
                || this.equals(LIST_TYPE)
                || this.equals(MAP_TYPE);
    }

    public Class<?> getCalzz() {
        return calzz;
    }

    public String getName() {
        return calzz.getSimpleName();
    }

    public String getBasicName() {
        return basicCode;
    }

    public static JDBCType getJdbcType(String clazzName) {
        JavaTypeEnum javaTypeEnum = codeToEnum(clazzName);
        if (javaTypeEnum == null) {
            return JDBCType.VARCHAR;
        }
        return javaTypeEnum.jdbcType;
    }
}
