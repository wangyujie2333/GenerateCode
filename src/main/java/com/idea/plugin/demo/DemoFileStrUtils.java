package com.idea.plugin.demo;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.JavaFileConfigVO;
import com.idea.plugin.setting.support.TableConfigVO;
import com.idea.plugin.sql.support.FieldInfoVO;
import com.idea.plugin.sql.support.IndexInfoVO;
import com.idea.plugin.sql.support.TableInfoVO;
import com.idea.plugin.sql.support.enums.NullTypeEnum;
import com.idea.plugin.sql.support.enums.PrimaryTypeEnum;
import com.idea.plugin.sql.support.enums.ProcedureTypeEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DemoFileStrUtils {
    public static String sqlFileStr(TableConfigVO configVO) {
        StringBuilder sqlFileStr = new StringBuilder("-- 创建sql文件配置参数\n" +
                "author:" + configVO.author + "\n" +
                "filePath:" + configVO.filePath + "\n" +
                "fileName:" + configVO.fileName + "\n" +
                "jdbcUrl:" + configVO.jdbcUrl + "\n" +
                "username:" + configVO.username + "\n" +
                "dbpasswd:" + configVO.dbpasswd + "\n\n");
        for (TableInfoVO tableInfoVO : configVO.tableInfoVOS) {
            if (configVO.procedureTypeList.contains(ProcedureTypeEnum.ADD_TABLE.name())) {
                StringBuilder tableInfoStr = new StringBuilder("-- 示例ADD_TABLE\n" +
                        "procedureType:ADD_TABLE\n" +
                        "comment:" + tableInfoVO.tableComment + "(" + tableInfoVO.tableName + ")" + ProcedureTypeEnum.ADD_TABLE.getName() + "\n" +
                        "tableInfo:" + tableInfoVO.tableName + "; " + tableInfoVO.tableComment + "\n");
                for (FieldInfoVO fieldInfo : tableInfoVO.getFieldInfos()) {
                    String args = null;
                    if (PrimaryTypeEnum.PRIMARY.equals(fieldInfo.primary)) {
                        args = PrimaryTypeEnum.PRIMARY.name();
                    } else if (NullTypeEnum.NOT_NULL.equals(fieldInfo.nullType)) {
                        args = NullTypeEnum.NOT_NULL.name();
                    }
                    if (args != null) {
                        tableInfoStr.append("fieldInfos:").append(fieldInfo.columnName).append("; ").append(fieldInfo.columnType.getType(fieldInfo.columnTypeArgs)).append("; ").append(fieldInfo.comment).append("; ").append(args).append("\n");
                    } else {
                        tableInfoStr.append("fieldInfos:").append(fieldInfo.columnName).append("; ").append(fieldInfo.columnType.getType(fieldInfo.columnTypeArgs)).append("; ").append(fieldInfo.comment).append("\n");
                    }
                }
                sqlFileStr.append(tableInfoStr).append("\n");
            }
            if (configVO.procedureTypeList.contains(ProcedureTypeEnum.ADD_INDEX.name())) {
                StringBuilder tableInfoStr = new StringBuilder("-- 示例ADD_INDEX\n" +
                        "procedureType:ADD_INDEX\n" +
                        "comment:" + tableInfoVO.tableComment + "(" + tableInfoVO.tableName + ")" + ProcedureTypeEnum.ADD_INDEX.getName() + "\n" +
                        "tableInfo:" + tableInfoVO.tableName + "; " + tableInfoVO.tableComment + "\n");
                for (IndexInfoVO indexInfo : tableInfoVO.getIndexInfos()) {
                    tableInfoStr.append("indexInfos:").append(indexInfo.indexName).append("; ").append(indexInfo.indexColumnName).append("\n");
                }
                sqlFileStr.append(tableInfoStr).append("\n");
            }
            if (configVO.procedureTypeList.contains(ProcedureTypeEnum.ADD_COLUMN.name())) {
                StringBuilder tableInfoStr = new StringBuilder("-- 示例ADD_COLUMN\n" +
                        "procedureType:ADD_COLUMN\n" +
                        "comment:" + tableInfoVO.tableComment + "(" + tableInfoVO.tableName + ")" + ProcedureTypeEnum.ADD_COLUMN.getName() + "\n" +
                        "tableInfo:" + tableInfoVO.tableName + "; " + tableInfoVO.tableComment + "\n");
                for (FieldInfoVO fieldInfo : tableInfoVO.getFieldInfos()) {
                    String args = null;
                    if (PrimaryTypeEnum.PRIMARY.equals(fieldInfo.primary)) {
                        args = PrimaryTypeEnum.PRIMARY.name();
                    } else if (NullTypeEnum.NOT_NULL.equals(fieldInfo.nullType)) {
                        args = NullTypeEnum.NOT_NULL.name();
                    }
                    if (args != null) {
                        tableInfoStr.append("fieldInfos:").append(fieldInfo.columnName).append("; ").append(fieldInfo.columnType.getType(fieldInfo.columnTypeArgs)).append("; ").append(fieldInfo.comment).append("; ").append(args).append("\n");
                    } else {
                        tableInfoStr.append("fieldInfos:").append(fieldInfo.columnName).append("; ").append(fieldInfo.columnType.getType(fieldInfo.columnTypeArgs)).append("; ").append(fieldInfo.comment).append("\n");
                    }
                }
                sqlFileStr.append(tableInfoStr).append("\n");
            }
            if (configVO.procedureTypeList.contains(ProcedureTypeEnum.MODIFY_COLUMN.name())) {
                StringBuilder tableInfoStr = new StringBuilder("-- 示例MODIFY_COLUMN\n" +
                        "procedureType:MODIFY_COLUMN\n" +
                        "comment:" + tableInfoVO.tableComment + "(" + tableInfoVO.tableName + ")" + ProcedureTypeEnum.MODIFY_COLUMN.getName() + "\n" +
                        "tableInfo:" + tableInfoVO.tableName + "; " + tableInfoVO.tableComment + "\n");
                for (FieldInfoVO fieldInfo : tableInfoVO.getFieldInfos()) {
                    String args = null;
                    if (PrimaryTypeEnum.PRIMARY.equals(fieldInfo.primary)) {
                        args = PrimaryTypeEnum.PRIMARY.name();
                    } else if (NullTypeEnum.NOT_NULL.equals(fieldInfo.nullType)) {
                        args = NullTypeEnum.NOT_NULL.name();
                    }
                    if (args != null) {
                        tableInfoStr.append("fieldInfos:").append(fieldInfo.columnName).append("; ").append(fieldInfo.columnType.getType(fieldInfo.columnTypeArgs)).append("; ").append(fieldInfo.comment).append("; ").append(args).append("\n");
                    } else {
                        tableInfoStr.append("fieldInfos:").append(fieldInfo.columnName).append("; ").append(fieldInfo.columnType.getType(fieldInfo.columnTypeArgs)).append("; ").append(fieldInfo.comment).append("\n");
                    }
                }
                sqlFileStr.append(tableInfoStr).append("\n");

            }
            if (configVO.procedureTypeList.contains(ProcedureTypeEnum.INSERT_DATA.name())) {
                String tableInfoStr = "-- 示例INSERT_DATA\n" +
                        "procedureType:INSERT_DATA\n" +
                        "comment:" + tableInfoVO.tableComment + "(" + tableInfoVO.tableName + ")" + ProcedureTypeEnum.INSERT_DATA.getName() + "\n" +
                        "tableInfo:" + tableInfoVO.tableName + "; " + tableInfoVO.tableComment + "\n" +
                        String.join("\n", tableInfoVO.insertData) + "\n";
                sqlFileStr.append(tableInfoStr).append("\n");
            }
            if (configVO.procedureTypeList.contains(ProcedureTypeEnum.INSERT_SQL.name())) {
                String tableInfoStr = "-- 示例INSERT_SQL\n" +
                        "procedureType:INSERT_SQL\n" +
                        "comment:" + tableInfoVO.tableComment + "(" + tableInfoVO.tableName + ")" + ProcedureTypeEnum.INSERT_SQL.getName() + "\n" +
                        "tableInfo:" + tableInfoVO.tableName + "; " + tableInfoVO.tableComment + "\n" +
                        "insertSql:" + tableInfoVO.insertSql + "\n\n";
                sqlFileStr.append(tableInfoStr).append("\n");

            }
            if (configVO.procedureTypeList.contains(ProcedureTypeEnum.ADD_DATA.name())) {
                String tableInfoStr = "-- 示例ADD_DATA\n" +
                        "procedureType:ADD_DATA\n" +
                        "comment:" + tableInfoVO.tableComment + "(" + tableInfoVO.tableName + ")" + ProcedureTypeEnum.ADD_DATA.getName() + "\n" +
                        "tableInfo:" + tableInfoVO.tableName + "; " + tableInfoVO.tableComment + "\n" +
                        "insertColumnName:" + tableInfoVO.insertColumnName + "; " + tableInfoVO.insertColumnParam + "\n\n";
                sqlFileStr.append(tableInfoStr).append("\n");
            }
        }
        return sqlFileStr.toString();
    }

    public static String javaFileStr(TableConfigVO configVO) {
        JavaFileConfigVO javaFileConfig = ToolSettings.getJavaFileConfig();
        StringBuilder javaFileStr = new StringBuilder("-- 创建java文件配置参数\n" +
                "author:" + configVO.author + "\n" +
                "modulePath:" + javaFileConfig.modulePath + "\n" +
                "modulePath:" + javaFileConfig.modulePath + "\n" +
                "methods:" + String.join(",", javaFileConfig.getMethods()) + "\n" +
                "doPath:" + javaFileConfig.doPath + "\n" +
                "voPath:" + javaFileConfig.voPath + "\n" +
                "daoPath:" + javaFileConfig.daoPath + "\n" +
                "iservicePath:" + javaFileConfig.iservicePath + "\n" +
                "servicePath:" + javaFileConfig.servicePath + "\n" +
                "controllerPath:" + javaFileConfig.controllerPath + "\n" +
                "controllerReturn:" + javaFileConfig.controllerReturn + "\n" +
                "jdbcUrl:" + configVO.jdbcUrl + "\n" +
                "username:" + configVO.username + "\n" +
                "dbpasswd:" + configVO.dbpasswd + "\n\n");
        for (TableInfoVO tableInfoVO : configVO.tableInfoVOS) {
            List<String> fileCreateTypes = Arrays.stream(ProcedureTypeEnum.values()).filter(procedureTypeEnum -> procedureTypeEnum.getFileCreateType() != null)
                    .map(Enum::name).collect(Collectors.toList());
            String fileCreateType = configVO.procedureTypeList.stream().filter(fileCreateTypes::contains).collect(Collectors.joining(", "));
            String tableInfoStr = "-- 示例" + fileCreateType + "\n" +
                    "procedureType:" + fileCreateType + "\n" +
                    "tableInfo:" + tableInfoVO.tableName + "\n" +
                    "interfaceClazz:\n";
            javaFileStr.append(tableInfoStr).append("\n");
        }
        return javaFileStr.toString();
    }

}
