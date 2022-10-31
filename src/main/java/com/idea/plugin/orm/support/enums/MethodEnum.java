package com.idea.plugin.orm.support.enums;

import com.google.common.base.CaseFormat;
import com.idea.plugin.setting.template.JavaTemplateVO;
import com.idea.plugin.sql.support.GeneralOrmInfoVO;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MethodEnum {
    INSERT("insert", "int", "%sDO", true),
    INSERT_BATCH("insertBatch", "int", "java.util.List<%sDO>"),
    UPDATE("update", "int", "%sDO", true),
    UPDATE_BY_IDS("updateByIds", "int", "java.util.List<java.lang.String>"),
    UPDATE_BATCH("updateBatch", "int", "java.util.List<%sDO>"),
    DELETE_BY_ID("deleteById", "int", "java.lang.String", true),
    DELETE_BY_IDS("deleteByIds", "int", "java.util.List<java.lang.String>"),
    DELETE_BY_DO("deleteByDO", "int", "%sDO"),
    SELECT_BY_ID("selectById", "%sDO", "java.lang.String", true),
    SELECT_BY_IDS("selectByIds", "%sDO", "java.util.List<java.lang.String>", true),
    SELECT_BY_DO("selectByDO", "%sDO", "%sDO", true),
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

    public String getRet(String str, JavaTemplateVO javaTemplateVO) {
        if (!ret.contains("%s")) {
            return ret;
        }
        String name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str.replaceAll("(^[A-Z]){1}[_]{1}", ""));
        String fileName = getFileName(javaTemplateVO);
        if (StringUtils.isEmpty(fileName)) {
            fileName = ret;
        }
        return String.format(fileName, name);
    }

    public String getParam(String str, JavaTemplateVO javaTemplateVO, GeneralOrmInfoVO generalOrmInfoVO) {
        if (!param.contains("%s")) {
            return param;
        }
        String name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str.replaceAll("(^[A-Z]){1}[_]{1}", ""));
        String fileName = getFileName(javaTemplateVO);
        if (StringUtils.isEmpty(fileName)) {
            fileName = param;
        }
        fileName = (FileTypePathEnum.DO.getJavapath(generalOrmInfoVO) + "/" + fileName).replaceAll("/", ".");
        return String.format(fileName, name);
    }

    private String getFileName(JavaTemplateVO javaTemplateVO) {
        String fileName = null;
        if (javaTemplateVO != null) {
            JavaTemplateVO.OrmTemplateVO ormTemplateVO;
            if (JavaTemplateVO.isJpa(javaTemplateVO)) {
                ormTemplateVO = javaTemplateVO.getJpa();
            } else {
                ormTemplateVO = javaTemplateVO.getMybatis();
            }
            fileName = ormTemplateVO.getDO();

        }
        return fileName;
    }


    public static List<String> getDefaultMthods() {
        return Arrays.stream(MethodEnum.values()).filter(methodEnum -> Boolean.TRUE.equals(methodEnum.def)).map(MethodEnum::getCode).collect(Collectors.toList());
    }

    public static MethodEnum codeToEnum(String code) {
        return Arrays.stream(MethodEnum.values()).filter(methodEnum -> methodEnum.getCode().equals(code.trim())).findAny().orElse(SELECT_BY_ID);
    }
}
