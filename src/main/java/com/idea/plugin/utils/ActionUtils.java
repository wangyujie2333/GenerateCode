package com.idea.plugin.utils;

import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.report.support.module.ReportFileInfo;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.setting.template.KeyTemplateVO;
import com.idea.plugin.sql.support.enums.FieldTypeEnum;
import com.idea.plugin.sql.support.enums.NullTypeEnum;
import com.idea.plugin.sql.support.enums.PrimaryTypeEnum;
import com.idea.plugin.translator.TranslatorFactroy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActionUtils {


    public static <T> T readGeneralInfoByText(ActionContext context, Class<T> clazz) {
        List<String> textList = context.getTextList();
        if (CollectionUtils.isEmpty(textList)) {
            throw new RuntimeException("数据不能为空");
        }
        List<String> dealTextList = new ArrayList<>();
        textList.stream().filter(text -> StringUtils.isNotEmpty(text) && !text.startsWith("#") && !text.startsWith("--"))
                .forEach(text -> {
                    text = text.replaceAll("，", ",");
                    text = text.replaceAll("；", ";");
                    text = text.replaceFirst("：", ":");
                    text = text.replaceAll("\\\\", "/");
                    int index = text.indexOf(":");
                    if ((text.toUpperCase().trim().startsWith("INSERT INTO"))) {
                        dealTextList.add("tableInfos.insertData:" + text);
                    } else if (index > 0) {
                        String propertyName = text.substring(0, index).trim();
                        Object[] args = Arrays.stream(text.substring(index + 1).split(";")).map(String::trim).filter(StringUtils::isNotEmpty).toArray();
                        if (propertyName.startsWith("procedureType")) {
                            if (args.length == 0 || args[0] == null || StringUtils.isEmpty(args[0].toString())) {
                                throw new RuntimeException("procedureType不能为空");
                            }
                            dealTextList.add("tableInfos." + propertyName + ":" + args[0].toString().toUpperCase());
                        } else if (propertyName.startsWith("comment")
                                || propertyName.startsWith("insertSql")
                                || propertyName.startsWith("interfaceClazz")) {
                            dealTextList.add("tableInfos." + text);
                        } else if (propertyName.startsWith("tableInfo")) {
                            if (args.length == 0 || args[0] == null || StringUtils.isEmpty(args[0].toString())) {
                                throw new RuntimeException("tableInfo不能为空");
                            }
                            String tableComment = TranslatorFactroy.translate(args[0].toString());
                            if (args.length == 2) {
                                if (args[1] != null && StringUtils.isNotEmpty(args[1].toString())) {
                                    tableComment = args[1].toString();
                                }
                            }
                            dealTextList.add("tableInfos.tableName:" + args[0].toString().toUpperCase());
                            dealTextList.add("tableInfos.tableComment:" + tableComment);
                        } else if (propertyName.startsWith("fieldInfos")) {
                            getFieldInfosStr(args, dealTextList);
                            dealTextList.add("tableInfos.fieldInfos.columnName:");
                        } else if (propertyName.startsWith("indexInfos")) {
                            if (args.length == 0 || args[0] == null || StringUtils.isEmpty(args[0].toString())) {
                                throw new RuntimeException("indexInfos不能为空");
                            }
                            String indexColumnName = "ID";
                            if (args.length == 2) {
                                if (args[1] != null && StringUtils.isNotEmpty(args[1].toString())) {
                                    indexColumnName = args[1].toString();
                                }
                            }
                            dealTextList.add("tableInfos.indexInfos.indexName:" + args[0].toString().toUpperCase());
                            dealTextList.add("tableInfos.indexInfos.indexColumnName:" + indexColumnName.toUpperCase());
                            dealTextList.add("tableInfos.indexInfos.indexName:");
                        } else if (propertyName.startsWith("insertColumnName")) {
                            if (args.length == 0 || args[0] == null || StringUtils.isEmpty(args[0].toString())) {
                                throw new RuntimeException("insertColumnName不能为空");
                            }
                            String insertColumnParam = "ID";
                            if (args.length == 2) {
                                if (args[1] != null && StringUtils.isNotEmpty(args[1].toString())) {
                                    insertColumnParam = args[1].toString();
                                }
                            }
                            dealTextList.add("tableInfos.insertColumnName:" + args[0].toString().toUpperCase());
                            dealTextList.add("tableInfos.insertColumnParam:" + insertColumnParam.toUpperCase());
                        } else if (propertyName.startsWith("methods")) {
                            List<String> methodList = Arrays.stream(text.substring(index + 1).split(",")).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(methodList)) {
                                for (String method : methodList) {
                                    dealTextList.add("methods:" + method);
                                }
                            } else {
                                dealTextList.add("methods:insert");
                                dealTextList.add("methods:update");
                                dealTextList.add("methods:deleteById");
                                dealTextList.add("methods:selectById");
                            }
                            dealTextList.add("");
                        } else {
                            dealTextList.add(text);
                        }
                    } else {
                        dealTextList.add(text);
                    }
                });
        if (dealTextList.stream().filter(text -> text.startsWith("tableInfos.procedureType")).count() == 1) {
            dealTextList.add("tableInfos.procedureType:");
        }
        return JsonUtil.fromJson(JsonUtil.getJsonStrByFileStr(dealTextList), clazz);
    }

    private static void getFieldInfosStr(Object[] args, List<String> dealTextList) {
        if (args.length == 0 || args[0] == null || StringUtils.isEmpty(args[0].toString())) {
            throw new RuntimeException("fieldInfos不能为空");
        }
        String columnName = args[0].toString();
        String columnType = "VARCHAR";
        String columnTypeArgs = "32";
        String comment = TranslatorFactroy.translate(args[0].toString());
        String nullType = NullTypeEnum.NULL.name();
        String primary = PrimaryTypeEnum.NON_PRIMARY.name();
        if (args.length >= 2) {
            columnType = FieldTypeEnum.codeToEnum(args[1].toString().toUpperCase()).name();
            columnTypeArgs = FieldTypeEnum.codeGetArgs(args[1].toString().toUpperCase());
        }
        if (args.length >= 3) {
            if (args[2] != null) {
                comment = TranslatorFactroy.translate(args[0].toString());
                if (NullTypeEnum.codeToEnum(args[2].toString()) != null) {
                    nullType = NullTypeEnum.codeToEnum(args[2].toString()).name();
                } else if (PrimaryTypeEnum.codeToEnum(args[2].toString()) != null) {
                    primary = PrimaryTypeEnum.codeToEnum(args[2].toString()).name();
                } else {
                    comment = args[2] == null ? TranslatorFactroy.translate(args[0].toString()) : args[2].toString();
                }
            }
        }
        if (args.length >= 4) {
            if (args[3] != null) {
                if (NullTypeEnum.codeToEnum(args[3].toString()) != null) {
                    nullType = NullTypeEnum.codeToEnum(args[3].toString()).name();
                }
                if (PrimaryTypeEnum.codeToEnum(args[3].toString()) != null) {
                    primary = PrimaryTypeEnum.codeToEnum(args[3].toString()).name();
                    if (StringUtils.isEmpty(comment)) {
                        comment = "主键ID";
                    }
                }
            }
        }

        if (args.length >= 5) {
            if (args[3] != null) {
                if (NullTypeEnum.codeToEnum(args[3].toString()) != null) {
                    nullType = NullTypeEnum.codeToEnum(args[3].toString()).name();
                }
            }
            if (args[4] != null) {
                if (PrimaryTypeEnum.codeToEnum(args[4].toString()) != null) {
                    primary = PrimaryTypeEnum.codeToEnum(args[3].toString()).name();
                    if (StringUtils.isEmpty(comment)) {
                        comment = "主键ID";
                    }
                }
            }
        }
        dealTextList.add("tableInfos.fieldInfos.columnName:" + columnName.toUpperCase());
        dealTextList.add("tableInfos.fieldInfos.columnType:" + columnType.toUpperCase());
        dealTextList.add("tableInfos.fieldInfos.columnTypeArgs:" + columnTypeArgs);
        dealTextList.add("tableInfos.fieldInfos.comment:" + comment);
        dealTextList.add("tableInfos.fieldInfos.nullType:" + nullType.toUpperCase());
        dealTextList.add("tableInfos.fieldInfos.primary:" + primary.toUpperCase());
    }

    public static ReportFileInfo readReportFileInfo(List<String> dayStrlist) {
        ReportConfigVO config = ToolSettings.getReportConfig();
        KeyTemplateVO keyTemplate = config.keyTemplate;
        boolean isdone = false;
        boolean istodo = false;
        boolean islearn = false;
        List<String> result = new ArrayList<>();
        try {
            for (String dayStr : dayStrlist) {
                if (StringUtils.isEmpty(dayStr) || dayStr.startsWith("--")) {
                    continue;
                }
                if (dayStr.startsWith(keyTemplate.done)) {
                    isdone = true;
                    istodo = false;
                    islearn = false;
                    continue;
                }
                if (dayStr.startsWith(keyTemplate.todo)) {
                    isdone = false;
                    istodo = true;
                    islearn = false;
                    continue;
                }
                if (dayStr.startsWith(keyTemplate.learn)) {
                    istodo = false;
                    isdone = false;
                    islearn = true;
                    dayStr = dayStr.replace(keyTemplate.learn, "learn:");
                    result.add(dayStr);
                    continue;
                }
                if (dayStr.startsWith(keyTemplate.summary)) {
                    istodo = false;
                    isdone = false;
                    islearn = false;
                    continue;
                }
                if (isdone) {
                    if (dayStr.startsWith(keyTemplate.need)) {
                        dayStr = dayStr.replace(keyTemplate.need, "done.need:");
                    }
                    if (dayStr.startsWith(keyTemplate.bug)) {
                        dayStr = dayStr.replace(keyTemplate.bug, "done.bug:");
                    }
                    if (dayStr.startsWith(keyTemplate.other)) {
                        dayStr = dayStr.replace(keyTemplate.other, "done.other:");
                    }
                    result.add(dayStr);
                }
                if (istodo) {
                    if (dayStr.startsWith(keyTemplate.need)) {
                        dayStr = dayStr.replace(keyTemplate.need, "todo.need:");
                    }
                    if (dayStr.startsWith(keyTemplate.bug)) {
                        dayStr = dayStr.replace(keyTemplate.bug, "todo.bug:");
                    }
                    if (dayStr.startsWith(keyTemplate.other)) {
                        dayStr = dayStr.replace(keyTemplate.other, "todo.other:");
                    }
                    result.add(dayStr);
                }
                if (islearn) {
                    result.add(dayStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonUtil.fromJson(JsonUtil.getJsonStrByFileStr(result), ReportFileInfo.class);
    }
}
