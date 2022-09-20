package com.idea.plugin.orm.support.enums;

import com.google.common.base.CaseFormat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MethodEnum {
    INSERT("insert", "int", "%sDO", true),
    UPDATE("update", "int", "%sDO", true),
    INSERT_BATCH("insertBatch", "int", "List<%sDO>"),
    UPDATE_BATCH("updateBatch", "int", "List<%sDO>"),
    DELETE_BY_ID("deleteById", "int", "String", true),
    DELETE_BY_IDS("deleteByIds", "int", "List<String>"),
    DELETE_BY_VO("deleteByVO", "int", "%sVO"),
    SELECT_BY_ID("selectById", "%sDO", "String", true),
    SELECT_BY_IDS("selectByIds", "%sDO", "List<String>"),
    SELECT_BY_DO("selectByVO", "%sDO", "%sVO"),
    ;
    private String code;
    private Boolean def;
    private String ret;
    private String param;

    MethodEnum(String code, String ret, String param) {
        this.code = code;
        this.ret = ret;
        this.param = param;
    }

    MethodEnum(String code, String ret, String param, Boolean def) {
        this.code = code;
        this.ret = ret;
        this.param = param;
        this.def = def;
    }

    public String getCode() {
        return code;
    }

    public Boolean getDef() {
        return def;
    }

    public String getRet(String str) {
        String name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str.replaceAll("(^[A-Z]){1}[_]{1}", ""));
        if (ret.contains("%s")) {
            return String.format(ret, name);
        }
        return ret;
    }

    public String getParam(String str) {
        String name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str.replaceAll("(^[A-Z]){1}[_]{1}", ""));
        if (param.contains("%s")) {
            return String.format(param, name);
        }
        return param;
    }

    public static List<String> getDefaultMthods() {
        return Arrays.stream(MethodEnum.values()).filter(methodEnum -> Boolean.TRUE.equals(methodEnum.def)).map(MethodEnum::getCode).collect(Collectors.toList());
    }

    public static MethodEnum codeToEnum(String code) {
        return Arrays.stream(MethodEnum.values()).filter(methodEnum -> methodEnum.getCode().equals(code.trim())).findAny().orElse(SELECT_BY_ID);
    }
}
