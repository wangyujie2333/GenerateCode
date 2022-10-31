package com.idea.plugin.orm.service;

import com.google.common.base.CaseFormat;
import com.idea.plugin.document.support.*;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.sql.support.enums.DataTypeEnum;
import com.idea.plugin.sql.support.enums.FileDDLTypeEnum;
import com.idea.plugin.utils.StringUtil;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiModifier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeneratorXmlFileStrService {

    public static String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
            "<mapper namespace=\"%s\">\n\n" +
            "%s" +
            "</mapper>\n\n";
    public static String resultMap = "    <resultMap id=\"BaseResultMap\" type=\"%s\">\n" +
            "%s" +
            "    </resultMap>\n\n";
    public static String resultMapVO = "    <resultMap id=\"BaseResultMapVO\" type=\"%s\">\n" +
            "%s" +
            "    </resultMap>\n\n";
    public static String sqlTable = "    <sql id=\"table_name\">\n" +
            "        %s\n" +
            "    </sql>\n\n";
    public static String sqlColmns = "    <sql id=\"base_column_list\">\n" +
            "%s\n" +
            "    </sql>\n\n";
    public static String insertSql = "    <insert id=\"%s\" %s>\n" +
            "        INSERT INTO\n" +
            "        <include refid=\"table_name\"/>\n" +
            "        (\n" +
            "        <include refid=\"base_column_list\"/>\n" +
            "        ) VALUES\n" +
            "        (\n" +
            "%s\n" +
            "        )\n" +
            "    </insert>\n\n";
    public static String updateSql = "    <update id=\"%s\" %s>\n" +
            "        UPDATE\n" +
            "        <include refid=\"table_name\"/>\n" +
            "        SET\n" +
            "%s\n" +
            "    </update>\n\n";
    public static String insertBatchMSql = "    <insert id=\"%s\" %s>\n" +
            "        INSERT INTO\n" +
            "        <include refid=\"table_name\"/>\n" +
            "        (\n" +
            "        <include refid=\"base_column_list\"/>\n" +
            "        )\n" +
            "        VALUES\n" +
            "        <foreach collection=\"%s\" item=\"item\" index=\"index\" separator=\",\">\n" +
            "            (\n" +
            "%s\n" +
            "            )\n" +
            "        </foreach>\n" +
            "    </insert>\n\n";
    public static String updateBatchMSql = "    <update id=\"%s\" %s>\n" +
            "        <foreach collection=\"%s\" separator=\";\" item=\"item\">\n" +
            "            UPDATE\n" +
            "            <include refid=\"table_name\"/>\n" +
            "            SET\n" +
            "%s\n" +
            "        </foreach>\n" +
            "    </update>\n\n";
    public static String insertBatchOSql = "    <insert id=\"%s\" %s>\n" +
            "        INSERT ALL\n" +
            "        <foreach collection=\"%s\" item=\"item\" separator=\" \">\n" +
            "            INTO\n" +
            "            <include refid=\"table_name\"/>\n" +
            "            (\n" +
            "            <include refid=\"base_column_list\"/>\n" +
            "            )\n" +
            "            VALUES\n" +
            "            (\n" +
            "%s\n" +
            "            )\n" +
            "        </foreach>\n" +
            "        SELECT * FROM dual\n" +
            "    </insert>\n\n";
    public static String updateBatchOSql = "    <update id=\"%s\" %s>\n" +
            "        BEGIN\n" +
            "        <foreach collection=\"%s\" separator=\";\" item=\"item\">\n" +
            "            UPDATE\n" +
            "            <include refid=\"table_name\"/>\n" +
            "            SET\n" +
            "%s\n" +
            "        </foreach>\n" +
            "        ;END;\n" +
            "    </update>\n\n";
    public static String deleteSql = "    <delete id=\"%s\">\n" +
            "        DELETE FROM\n" +
            "        <include refid=\"table_name\"/>\n" +
            "%s" +
            "    </delete>\n\n";
    public static String selectSql = "    <select id=\"%s\" %s %s>\n" +
            "        SELECT\n" +
            "        <include refid=\"base_column_list\"/>\n" +
            "        FROM\n" +
            "        <include refid=\"table_name\"/>\n" +
            "%s" +
            "    </select>\n\n";
    public static String whereSql = "        <where>\n" +
            "%s\n" +
            "        </where>\n";
    public static String ifSql = "        <if test=\"%s\">\n" +
            "%s\n" +
            "        </if>\n";
    public static String foreachSql = "        <foreach collection=\"%s\" item=\"item\" separator=\"OR\" open=\"AND (\" close=\")\">\n" +
            "            %s = #{item, jdbcType=VARCHAR}\n" +
            "        </foreach>\n";
    public static String foreachSqlMulti = " (%s IN\n" +
            "        <foreach collection=\"%s\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">\n" +
            "            <if test=\"(index %% 999) == 998\">NULL) OR %s IN (</if>\n" +
            "            #{item}\n" +
            "        </foreach>\n" +
            "        )\n";

    public static ClazzInfoDOVO getDOInfo(GeneratorContext context) {
        ClazzInfoDOVO clazzInfoDOVO = context.getClazzInfoVO().getClazzInfoDOVO();
        if (!clazzInfoDOVO.getClazzName().contains(".") && StringUtils.isNotEmpty(context.getClazzInfoVO().getPackageName())) {
            clazzInfoDOVO.setClazzName(context.getClazzInfoVO().getPackageName() + "." + clazzInfoDOVO.getClazzName());
        }
        return clazzInfoDOVO;
    }

    public static List<FieldInfoVO> getFieldInfoVOS(GeneratorContext context) {
        if (getDOInfo(context) == null) {
            return context.getTableInfoVO().getFieldInfos().stream().map(fieldInfoVO -> {
                com.idea.plugin.document.support.FieldInfoVO fieldInfoVO1 = new com.idea.plugin.document.support.FieldInfoVO();
                String fieldName = StringUtil.textToCamelCase(fieldInfoVO.getColumnName(), false);
                fieldInfoVO1.setFieldName(fieldName);
                return fieldInfoVO1;
            }).collect(Collectors.toList());
        }
        return getDOInfo(context).getFieldinfos();
    }

    public static String getXmlFileStr(GeneratorContext context, List<String> methodNames) {
        StringBuilder bodyInfoStr = new StringBuilder();
        bodyInfoStr.append(getXmlResultMapStr(context));
        bodyInfoStr.append(getXmlResultMapVOStr(context));
        bodyInfoStr.append(getXmlSqlTableStr(context));
        bodyInfoStr.append(getXmlSqlColmnsStr(context));
        bodyInfoStr.append(getXmlMethodStr(context, methodNames));
        return String.format(xmlHead, context.getClazzInfoVO().getResourceClazz(), bodyInfoStr);
    }

    public static String getXmlBatchMethodStr(GeneratorContext context, String dataType) {
        ClazzInfoVO clazzInfoVO = context.getClazzInfoVO();
        StringBuilder methodInfoStr = new StringBuilder();
        clazzInfoVO.getMethodInfos().forEach(methodInfoVO -> {
            if (!methodInfoVO.getMethodName().endsWith("Batch")) {
                return;
            }
            String parameter = "";
            String parameterType = "";
            Map<String, String> methodParameter = methodInfoVO.getMethodParameter();
            if (MapUtils.isNotEmpty(methodParameter) && methodParameter.size() == 1) {
                parameter = methodParameter.values().stream().findFirst().get();
                parameter = getClazzType(parameter);
                parameterType = String.format("parameterType=\"%s\"", parameter);
            }
            String sql;
            if (methodInfoVO.getMethodName().startsWith(FileDDLTypeEnum.INSERT.getCode())) {
                sql = insertBatchMSql;
                if (dataType.equals(DataTypeEnum.MYSQL.name())) {
                    sql = insertBatchOSql;
                }
                methodInfoStr.append(getXmlBatchInsertSqlStr(context, sql, methodInfoVO.getMethodName(), parameter, parameterType));
            } else if (methodInfoVO.getMethodName().startsWith(FileDDLTypeEnum.UPDATE.getCode())) {
                sql = updateBatchMSql;
                if (dataType.equals(DataTypeEnum.MYSQL.name())) {
                    sql = updateBatchOSql;
                }
                methodInfoStr.append(getXmlBatchUpdateSqlStr(context, sql, methodInfoVO.getMethodName(), parameter, parameterType));
            }
        });
        return methodInfoStr.toString();
    }

    public static String getXmlMethodStr(GeneratorContext context, List<String> methodNames) {
        ClazzInfoVO clazzInfoVO = context.getClazzInfoVO();
        StringBuilder methodInfoStr = new StringBuilder();
        clazzInfoVO.getMethodInfos().forEach(methodInfoVO -> {
            if (methodInfoVO.getMethodName().contains("Batch")) {
                return;
            }
            if (methodNames.contains(methodInfoVO.getMethodName())) {
                String parameterType = "";
                Map<String, String> methodParameter = methodInfoVO.getMethodParameter();
                if (MapUtils.isNotEmpty(methodParameter) && methodParameter.size() == 1) {
                    String paramName = methodParameter.values().stream().findFirst().get();
                    paramName = getClazzType(paramName);
                    parameterType = String.format("parameterType = \"%s\"", paramName);
                }
                String resultCondition = getResultCondition(context, clazzInfoVO, methodInfoVO);
                if (methodInfoVO.getMethodName().startsWith(FileDDLTypeEnum.INSERT.getCode())) {
                    methodInfoStr.append(getXmlInsertSqlStr(context, methodInfoVO.getMethodName(), parameterType));
                } else if (methodInfoVO.getMethodName().startsWith(FileDDLTypeEnum.UPDATE.getCode())) {
                    methodInfoStr.append(getXmlUpdateSqlStr(context, methodInfoVO.getMethodName(), parameterType,resultCondition));
                } else if (methodInfoVO.getMethodName().startsWith(FileDDLTypeEnum.DELETE.getCode())) {
                    methodInfoStr.append(getXmlDeleteSqlStr(methodInfoVO.getMethodName(), resultCondition));
                } else {
                    String methodReturn = methodInfoVO.getMethodReturn();
                    methodReturn = getClazzType(methodReturn);
                    String resultType = "resultType = \"" + methodReturn + "\"";
                    if (methodReturn.endsWith("DO")) {
                        resultType = "resultMap = \"BaseResultMap\"";
                    } else if (methodReturn.endsWith("VO")) {
                        resultType = "resultMap = \"BaseResultMapVO\"";
                    }
                    methodInfoStr.append(getXmlSelectSqlStr(methodInfoVO.getMethodName(), parameterType, resultType, resultCondition));
                }
            }
        });
        return methodInfoStr.toString();
    }

    private static String getResultCondition(GeneratorContext context, ClazzInfoVO clazzInfoVO, MethodInfoVO methodInfoVO) {
        List<String> methodParameterInfos = methodInfoVO.getMethodParameterInfos();
        String result = "";
        boolean containsIf = false;
        if (CollectionUtils.isNotEmpty(methodParameterInfos)) {
            for (String value : methodParameterInfos) {
                Pattern compile = Pattern.compile("([\\w,<>.]+)");
                Matcher matcher = compile.matcher(value);
                List<String> params = new ArrayList<>();
                while (matcher.find()) {
                    params.add(matcher.group(1));
                }
                String param;
                String type;
                if (params.contains("Param")) {
                    param = params.get(1);
                    type = params.get(2);
                } else {
                    param = params.get(1);
                    type = params.get(0);
                }
                String finalParam = param;
                FieldInfoVO idFieldInfoVO = getFieldInfoVOS(context).stream().filter(fieldInfoVO ->
                                finalParam.equalsIgnoreCase(fieldInfoVO.getFieldName()) || finalParam.startsWith(fieldInfoVO.getFieldName()))
                        .findFirst().orElse(getFieldInfoVOS(context).get(0));
                JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(type);
                String resultCondition = "";
                if (javaTypeEnum != null) {
                    if (JavaTypeEnum.LIST_TYPE.equals(javaTypeEnum)) {
                        resultCondition = "        AND" + getXmlForeachSqlMultiStr(param, StringUtil.textToConstant(idFieldInfoVO.getFieldName()));
                    } else {
                        resultCondition = String.format("        AND %s = #{%s, jdbcType = %s}\n", StringUtil.textToConstant(idFieldInfoVO.getFieldName()), param, JavaTypeEnum.getJdbcType(type).name());
                    }
                } else {
                    if (context.getTableInfoVO() == null) {
                        List<ClazzInfoVO> clazzInfoVOS = JavaDocConfig.getClazzInfoVOS(context.getProject(), clazzInfoVO.getClazzSimpleType(type));
                        if (clazzInfoVOS.size() > 0) {
                            List<FieldInfoVO> fieldinfos = clazzInfoVOS.get(0).getFieldinfos();
                            fieldinfos = fieldinfos.stream().filter(fieldInfoVO -> !(fieldInfoVO.getPsiField() instanceof PsiEnumConstant
                                    || fieldInfoVO.getPsiField().hasModifierProperty(PsiModifier.STATIC)
                                    || fieldInfoVO.getPsiField().hasModifierProperty(PsiModifier.FINAL))).collect(Collectors.toList());
                            String conditon = fieldinfos.stream().map(fieldInfoVO -> {
                                JavaTypeEnum fieldType = JavaTypeEnum.codeToEnum(fieldInfoVO.getFieldType());
                                String conditions;
                                String test;
                                if (JavaTypeEnum.LIST_TYPE.equals(fieldType)) {
                                    test = String.format("%s != null and !%s.isEmpty", fieldInfoVO.getFieldName(), fieldInfoVO.getFieldName());
                                    conditions = getXmlForeachSqlStr(finalParam, StringUtil.textToConstant(fieldInfoVO.fieldName));
                                    conditions = Arrays.stream(conditions.split("\n")).map(s -> "     " + s).collect(Collectors.joining("\n"));
                                } else {
                                    if (JavaTypeEnum.DATE_TYPE.equals(fieldType)) {
                                        test = String.format("%s != null", fieldInfoVO.getFieldName(), fieldInfoVO.getFieldName());
                                    } else {
                                        test = String.format("%s != null and !%s != ''", fieldInfoVO.getFieldName(), fieldInfoVO.getFieldName());
                                    }
                                    conditions = String.format("            AND %s = #{%s, jdbcType = %s}", StringUtil.textToConstant(fieldInfoVO.fieldName), finalParam, fieldInfoVO.getFieldJdbcType());
                                }
                                return getXmlIfSqlStr(test, conditions);
                            }).collect(Collectors.joining(""));
                            containsIf = true;
                            resultCondition = conditon;
                        } else {
                            resultCondition = String.format("        AND %s = #{%s, jdbcType = %s}", StringUtil.textToConstant(idFieldInfoVO.fieldName), param, idFieldInfoVO.getFieldJdbcType());
                        }
                    } else {
                        String conditon = context.getTableInfoVO().getFieldInfos().stream().map(fieldInfoVO -> {
                            JDBCType jtype = fieldInfoVO.getColumnType().getJtype();
                            String fieldName = StringUtil.textToCamelCase(fieldInfoVO.getColumnName(), false);
                            String conditions;
                            String test;
                            if (JavaTypeEnum.DATE_TYPE.equals(fieldInfoVO.getColumnType())) {
                                test = String.format("%s != null", fieldName, fieldName);
                            } else {
                                test = String.format("%s != null and !%s != ''", fieldName, fieldName);
                            }
                            conditions = String.format("            AND %s = #{%s, jdbcType = %s}", fieldInfoVO.getColumnName(), finalParam, jtype);
                            return getXmlIfSqlStr(test, conditions);
                        }).collect(Collectors.joining(""));
                        containsIf = true;
                        resultCondition = conditon;
                    }
                }
                result = result + resultCondition;
            }
        }
        if (containsIf) {
            result = getXmlWhereSqlStr(result);
        } else if (methodParameterInfos.size() == 1) {
            result = result.replace("AND", "WHERE");
        } else {
            result = getXmlWhereSqlStr(result);
        }
        return result;
    }

    @NotNull
    private static String getClazzType(String paramName) {
        JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(paramName);
        if (javaTypeEnum != null) {
            if (JavaTypeEnum.LIST_TYPE.equals(javaTypeEnum)) {
                Matcher matcher = ClazzInfoVO.paramPattern.matcher(paramName);
                if (matcher.find()) {
                    paramName = matcher.group(1);
                }
                javaTypeEnum = JavaTypeEnum.codeToEnum(paramName);
                if (javaTypeEnum != null) {
                    paramName = javaTypeEnum.getCalzz().getName();
                }
            } else {
                paramName = javaTypeEnum.getCalzz().getName();
            }
        }
        return paramName;
    }

    public static String getXmlResultMapStr(GeneratorContext context) {
        StringBuilder field = new StringBuilder();
        List<FieldInfoVO> fieldinfos = getDOInfo(context).getFieldinfos();
        for (int i = 0; i < fieldinfos.size(); i++) {
            FieldInfoVO fieldInfoVO = fieldinfos.get(i);
            String columnName = StringUtil.textToConstant(fieldInfoVO.getFieldName());
            String jdbcType = fieldInfoVO.getFieldJdbcType();
            String name = fieldInfoVO.getFieldName();
            String javaType = fieldInfoVO.getFieldType();
            if (i == 0) {
                field.append(String.format("        <id column=\"%s\" jdbcType=\"%s\" property=\"%s\" javaType=\"%s\"/>\n", columnName, jdbcType, name, javaType));
            } else {
                field.append(String.format("        <result column=\"%s\" jdbcType=\"%s\" property=\"%s\" javaType=\"%s\"/>\n", columnName, jdbcType, name, javaType));
            }
        }
        return String.format(resultMap, getDOInfo(context).getClazzName(), field);
    }

    public static String getXmlResultMapVOStr(GeneratorContext context) {
        StringBuilder field = new StringBuilder();
        List<FieldInfoVO> fieldinfos = getDOInfo(context).getVosFieldinfos();
        if (CollectionUtils.isNotEmpty(fieldinfos)) {
            for (int i = 0; i < fieldinfos.size(); i++) {
                FieldInfoVO fieldInfoVO = fieldinfos.get(i);
                String columnName = StringUtil.textToConstant(fieldInfoVO.getFieldName());
                String jdbcType = fieldInfoVO.getFieldJdbcType();
                String name = fieldInfoVO.getFieldName();
                String javaType = fieldInfoVO.getFieldType();
                if (i == 0) {
                    field.append(String.format("        <id column=\"%s\" jdbcType=\"%s\" property=\"%s\" javaType=\"%s\"/>\n", columnName, jdbcType, name, javaType));
                } else {
                    field.append(String.format("        <result column=\"%s\" jdbcType=\"%s\" property=\"%s\" javaType=\"%s\"/>\n", columnName, jdbcType, name, javaType));
                }
            }
            return String.format(resultMapVO, getDOInfo(context).getClazzVOName(), field);
        }
        return "";
    }

    public static String getXmlSqlTableStr(GeneratorContext context) {
        return String.format(sqlTable, getDOInfo(context).getTableName());
    }

    public static String getXmlSqlColmnsStr(GeneratorContext context) {
        List<FieldInfoVO> fieldinfos = getDOInfo(context).getFieldinfos();
        String columnNames = fieldinfos.stream().map(fieldInfoVO -> "        " + StringUtil.textToConstant(fieldInfoVO.getFieldName())).collect(Collectors.joining(",\n"));
        return String.format(sqlColmns, columnNames);
    }

    public static String getXmlInsertSqlStr(GeneratorContext context, String methodName, String parameterType) {
        List<String> fieldList = new ArrayList<>();
        ClazzInfoDOVO doInfo = getDOInfo(context);
        if (doInfo == null && context.getTableInfoVO() != null) {
            for (int i = 0; i < context.getTableInfoVO().getFieldInfos().size(); i++) {
                String jdbcType = context.getTableInfoVO().getFieldInfos().get(i).getColumnType().name();
                String name = context.getTableInfoVO().getFieldInfos().get(i).getColumnName();
                name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
                fieldList.add(String.format("        #{item.%s, jdbcType=%s}", name, jdbcType));
            }
        } else {
            List<FieldInfoVO> fieldinfos = doInfo.getFieldinfos();
            for (int i = 0; i < fieldinfos.size(); i++) {
                FieldInfoVO fieldInfoVO = fieldinfos.get(i);
                String jdbcType = fieldInfoVO.getFieldJdbcType();
                String name = fieldInfoVO.getFieldName();
                fieldList.add(String.format("        #{%s, jdbcType=%s}", name, jdbcType));
            }
        }
        return String.format(insertSql, methodName, parameterType, String.join(",\n", fieldList));
    }


    public static String getXmlUpdateSqlStr(GeneratorContext context, String methodName, String parameterType, String resultCondition) {
        List<String> fieldList = new ArrayList<>();
        ClazzInfoDOVO doInfo = getDOInfo(context);
        String columnName;
        String jdbcType;
        String name;
        if (doInfo == null && context.getTableInfoVO() != null) {
            for (int i = 0; i < context.getTableInfoVO().getFieldInfos().size(); i++) {
                jdbcType = context.getTableInfoVO().getFieldInfos().get(i).getColumnType().name();
                columnName = context.getTableInfoVO().getFieldInfos().get(i).getColumnName();
                name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
                fieldList.add(String.format("        #{item.%s, jdbcType=%s}", name, jdbcType));
            }
            columnName = context.getTableInfoVO().getFieldInfos().get(0).getColumnName();
            jdbcType = context.getTableInfoVO().getFieldInfos().get(0).getColumnType().name();
            name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
        } else {
            List<FieldInfoVO> fieldinfos = doInfo.getFieldinfos();
            for (int i = 1; i < fieldinfos.size(); i++) {
                FieldInfoVO fieldInfoVO = fieldinfos.get(i);
                columnName = StringUtil.textToConstant(fieldInfoVO.getFieldName());
                jdbcType = fieldInfoVO.getFieldJdbcType();
                name = fieldInfoVO.getFieldName();
                fieldList.add(String.format("        %s = #{%s, jdbcType=%s}", columnName, name, jdbcType));
            }
        }
        fieldList.add(resultCondition);
        return String.format(updateSql, methodName, parameterType, String.join(",\n", fieldList));
    }

    public static String getXmlBatchInsertSqlStr(GeneratorContext context, String sql, String methodName, String parameter, String parameterType) {
        List<String> fieldList = new ArrayList<>();
        ClazzInfoDOVO doInfo = getDOInfo(context);
        if (doInfo == null && context.getTableInfoVO() != null) {
            for (int i = 0; i < context.getTableInfoVO().getFieldInfos().size(); i++) {
                String jdbcType = context.getTableInfoVO().getFieldInfos().get(i).getColumnType().name();
                String name = context.getTableInfoVO().getFieldInfos().get(i).getColumnName();
                name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
                fieldList.add(String.format("            #{item.%s, jdbcType=%s}", name, jdbcType));
            }
        } else {
            List<FieldInfoVO> fieldinfos = doInfo.getFieldinfos();
            for (int i = 0; i < fieldinfos.size(); i++) {
                FieldInfoVO fieldInfoVO = fieldinfos.get(i);
                String jdbcType = fieldInfoVO.getFieldJdbcType();
                String name = fieldInfoVO.getFieldName();
                fieldList.add(String.format("            #{item.%s, jdbcType=%s}", name, jdbcType));
            }
        }
        return String.format(sql, methodName, parameterType, parameter, String.join(",\n", fieldList));
    }

    public static String getXmlBatchUpdateSqlStr(GeneratorContext context, String sql, String methodName, String parameter, String parameterType) {
        List<String> fieldList = new ArrayList<>();
        ClazzInfoDOVO doInfo = getDOInfo(context);
        String columnName;
        String jdbcType;
        String name;
        if (doInfo == null && context.getTableInfoVO() != null) {
            for (int i = 0; i < context.getTableInfoVO().getFieldInfos().size(); i++) {
                columnName = context.getTableInfoVO().getFieldInfos().get(i).getColumnName();
                jdbcType = context.getTableInfoVO().getFieldInfos().get(i).getColumnType().name();
                name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
                fieldList.add(String.format("            #{item.%s, jdbcType=%s}", name, jdbcType));
            }
            columnName = context.getTableInfoVO().getFieldInfos().get(0).getColumnName();
            jdbcType = context.getTableInfoVO().getFieldInfos().get(0).getColumnType().name();
            name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
        } else {
            List<FieldInfoVO> fieldinfos = doInfo.getFieldinfos();
            for (int i = 1; i < fieldinfos.size(); i++) {
                FieldInfoVO fieldInfoVO = fieldinfos.get(i);
                name = fieldInfoVO.getFieldName();
                columnName = StringUtil.textToConstant(name);
                jdbcType = fieldInfoVO.getFieldJdbcType();
                fieldList.add(String.format("            %s = #{item.%s, jdbcType=%s}", columnName, name, jdbcType));
            }
            FieldInfoVO fieldInfoVO = fieldinfos.get(0);
            name = fieldInfoVO.getFieldName();
            columnName = StringUtil.textToConstant(name);
            jdbcType = fieldInfoVO.getFieldJdbcType();
        }
        fieldList.add(String.format("            WHERE %s = #{item.%s, jdbcType=%s}", columnName, name, jdbcType));
        return String.format(sql, methodName, parameterType, parameter, String.join(",\n", fieldList));
    }

    public static String getXmlDeleteSqlStr(String methodName, String condition) {
        return String.format(deleteSql, methodName, condition);
    }

    public static String getXmlSelectSqlStr(String methodName, String parameterType, String resultType, String condition) {
        return String.format(selectSql, methodName, parameterType, resultType, condition);
    }

    public static String getXmlWhereSqlStr(String condition) {
        condition = Arrays.stream(condition.split("\n")).map(s -> "     " + s).collect(Collectors.joining("\n"));
        return String.format(whereSql, condition);
    }

    public static String getXmlIfSqlStr(String test, String condition) {
        return String.format(ifSql, test, condition);
    }

    public static String getXmlForeachSqlStr(String param, String columnName) {
        return String.format(foreachSql, param, columnName);
    }

    public static String getXmlForeachSqlMultiStr(String param, String columnName) {
        return String.format(foreachSqlMulti, columnName, param, columnName);
    }

}
