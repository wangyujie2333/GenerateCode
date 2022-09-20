package com.idea.plugin.orm.support.enums;

import com.google.common.base.CaseFormat;
import com.idea.plugin.sql.support.GeneralOrmInfoVO;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FileTypePathEnum {
    DO("/model/entity", "dto/do.ftl", "%sDO", FileTypeEnum.JAVA),
    VO("/model/vo", "dto/vo.ftl", "%sVO", FileTypeEnum.JAVA),
    DAO("/dao", "orm/dao.ftl", "I%sDAO", FileTypeEnum.JAVA),
    MAPPER("/dao", "orm/mapper.ftl", "I%sDAO", FileTypeEnum.XML),
    MAPPER_MYSQL("/dao/mysql", "orm/mapperMysql.ftl", "I%sDAO_mysql", FileTypeEnum.XML),
    MAPPER_ORACLE("/dao/oracle", "orm/mapperOracle.ftl", "I%sDAO_oracle", FileTypeEnum.XML),
    ISERVICE("/service", "service/iservice.ftl", "I%sService", FileTypeEnum.JAVA),
    SERVICE("/service/impl", "service/service.ftl", "%sService", FileTypeEnum.JAVA),
    CONTROLLER("/controller", "service/controller.ftl", "%sController", FileTypeEnum.JAVA),
    ;

    private static final Pattern compile = Pattern.compile("([(?i)I]{0,1})(\\w+)((?i)dao|service|controller)");

    private String javapath;
    private String ftlpath;
    private String fileName;
    private FileTypeEnum fileType;

    FileTypePathEnum(String javapath, String ftlpath, String fileName, FileTypeEnum fileType) {
        this.javapath = javapath;
        this.ftlpath = ftlpath;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getJavapath(GeneralOrmInfoVO generalOrmInfoVO) {
        String subpath;
        switch (this) {
            case DO:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.doPath) ? javapath : generalOrmInfoVO.doPath;
                break;
            case VO:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.voPath) ? javapath : generalOrmInfoVO.voPath;
                break;
            case DAO:
            case MAPPER:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.daoPath) ? javapath : generalOrmInfoVO.daoPath;
                break;
            case MAPPER_MYSQL:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.daoMysqlPath) ? javapath : generalOrmInfoVO.daoMysqlPath;
                break;
            case MAPPER_ORACLE:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.daoOraclePath) ? javapath : generalOrmInfoVO.daoOraclePath;
                break;
            case ISERVICE:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.iservicePath) ? javapath : generalOrmInfoVO.iservicePath;
                break;
            case SERVICE:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.servicePath) ? javapath : generalOrmInfoVO.servicePath;
                break;
            case CONTROLLER:
                subpath = StringUtils.isEmpty(generalOrmInfoVO.controllerPath) ? javapath : generalOrmInfoVO.controllerPath;
                break;
            default:
                subpath = javapath;
                break;
        }
        if (subpath.startsWith("/")) {
            subpath = subpath.substring(1);
        }
        return subpath;
    }

    public String getFtlpath() {
        return ftlpath;
    }

    public String getFileName(String tableName) {
        String name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.replaceAll("(^[A-Z]){1}[_]{1}", ""));
        return String.format(fileName, name);
    }

    public String getClazzFileName(String clazzName) {
        return String.format(fileName, getClazzEntiyName(clazzName));
    }

    public String getClazzEntiyName(String clazzName) {
        if (clazzName == null) {
            return "";
        }
        Matcher matcher = compile.matcher(clazzName);
        if (matcher.find()) {
            clazzName = matcher.group(2);
        }
        return clazzName;
    }

    public FileTypeEnum getFileType() {
        return fileType;
    }

}
