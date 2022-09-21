package com.idea.plugin.sql.support;

import com.idea.plugin.report.support.TemplateModule;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.template.SqlTemplateVO;
import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.support.enums.DataTypeEnum;
import com.idea.plugin.sql.support.enums.ProcedureTypeEnum;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SqlTemplateModeule extends TemplateModule {

    private String filePath;
    private GeneralSqlInfoVO generalSqlInfoVO;
    private TableSqlInfoVO tableSqlInfoVO;
    private FieldInfoVO fieldInfoVO;
    private IndexInfoVO indexInfoVO;
    private SqlTemplateVO sqlTemplateVO;
    private AbstractProcedureService procedureService;

    public SqlTemplateModeule(GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO) {
        this.generalSqlInfoVO = generalSqlInfoVO;
        this.tableSqlInfoVO = tableSqlInfoVO;
        this.sqlTemplateVO = ToolSettings.getReportConfig().sqlTemplateVO;
    }

    public Optional<SqlTemplateVO.SqlTemplate> getSqlTemplate() {
        if (sqlTemplateVO == null) {
            this.sqlTemplateVO = ToolSettings.getReportConfig().sqlTemplateVO;
        }
        if (sqlTemplateVO != null) {
            return sqlTemplateVO.getSqlTemplates().stream().filter(sqlTemplate ->
                    generalSqlInfoVO.getProcedureTypeEnum().name().equals(sqlTemplate.getProcedureType())).findAny();
        }
        return Optional.empty();
    }

    public Boolean isMerge() {
        if (getSqlTemplate().isPresent()) {
            return Boolean.TRUE.equals(getSqlTemplate().get().getIsMerge());
        }
        return true;
    }

    public Boolean isParam() {
        if (getSqlTemplate().isPresent()) {
            return Boolean.TRUE.equals(getSqlTemplate().get().getIsParam());
        }
        return false;
    }

    public Boolean isMysql() {
        return DataTypeEnum.MYSQL.equals(generalSqlInfoVO.getDataType());
    }

    public Boolean isOracle() {
        return DataTypeEnum.ORACLE.equals(generalSqlInfoVO.getDataType());
    }

    public SqlTemplateModeule() {
        this.sqlTemplateVO = ToolSettings.getReportConfig().sqlTemplateVO;
    }

    public String comment() {
        ProcedureTypeEnum procedureTypeEnum = generalSqlInfoVO.getProcedureTypeEnum();
        DataTypeEnum dataType = generalSqlInfoVO.getDataType();
        String comment = procedureTypeEnum.getName();
        Optional<SqlTemplateVO.SqlTemplate> sqlTemplate = getSqlTemplate();
        if (sqlTemplate.isPresent()) {
            comment = sqlTemplate.get().getComment();
        }
        comment = StringUtils.isEmpty(tableSqlInfoVO.comment) ? tableSqlInfoVO.tableComment + "(" + tableSqlInfoVO.tableName + ")" + comment : tableSqlInfoVO.comment;
        return "\n-- " + dataType.name() + ' ' + comment + "\n";
    }

    public String shortName() {
        String shortName = tableSqlInfoVO.tableShortName();
        if (isMerge()) {
            shortName = "TABLE";
        }
        return shortName;
    }

    public String tableName() {
        return tableSqlInfoVO.getTableName();
    }

    public String columnName() {
        return fieldInfoVO.getColumnName();
    }

    public String columnType() {
        if (isMysql()) {
            return fieldInfoVO.columnType.getMtype(fieldInfoVO.columnTypeArgs);
        } else if (isOracle()) {
            return fieldInfoVO.columnType.getOtype(fieldInfoVO.columnTypeArgs);
        }
        return fieldInfoVO.columnType.getMtype(fieldInfoVO.columnTypeArgs);
    }

    public String nullType() {
        return fieldInfoVO.nullType.getCode();
    }

    public String fcomment() {
        return fieldInfoVO.comment;
    }

    public String indexName() {
        return indexInfoVO.indexName;
    }

    public String indexColumnName() {
        return indexInfoVO.indexColumnName;
    }

    public String columnParams() {
        String columnParams = "";
        if (isParam() && tableSqlInfoVO.insertColumnParam != null) {
            String[] columnParamArr = tableSqlInfoVO.insertColumnParam.split(",");
            if (isMysql()) {
                columnParams = Arrays.stream(columnParamArr).map(columnParam -> "P_" + columnParam.trim() + " TINYTEXT").collect(Collectors.joining(", "));
                columnParams = "(" + columnParams + ")";
            } else if (isOracle()) {
                columnParams = Arrays.stream(columnParamArr).map(columnParam -> "P_" + columnParam.trim() + " IN VARCHAR").collect(Collectors.joining(", "));
                columnParams = "(" + columnParams + ") AS";
            }
        } else {
            if (isMysql()) {
                columnParams = columnParams;
            } else if (isOracle()) {
                columnParams = columnParams + " IS";
            }
        }
        return columnParams;
    }

    public String columnCondition() {
        String columnCondition = "PARAM = P_PARAM";
        if (isParam() && tableSqlInfoVO.insertColumnParam != null) {
            String[] columnParamArr = tableSqlInfoVO.insertColumnParam.split(",");
            columnCondition = columnParamArr[0].trim() + " = " + "P_" + columnParamArr[0].trim();
            String condition = "                                           WHERE %s\n                                             AND NOT EXISTS(SELECT 1 FROM %s T WHERE T.%s);";
            return String.format(condition, columnCondition, tableSqlInfoVO.tableName, columnCondition);
        }
        return ";";
    }

    public String param() {
        String param = "";
        if (isParam() && tableSqlInfoVO.insertColumnParam != null) {
            param = "'CLOMUN_VALUE'";
        }
        return param;
    }

    public String columnNameDeclare() {
        String[] columnNameArr = tableSqlInfoVO.insertColumnName.split(",");
        int maxCode = Arrays.stream(columnNameArr).max(Comparator.comparing(String::length)).get().trim().length();
        List<String> columnNameDeclares = new ArrayList<>();
        columnNameDeclares.add("TABLE_ID");
        columnNameDeclares.addAll(Arrays.asList(columnNameArr));
        if (isMysql()) {
            return columnNameDeclares.stream().map(columnName -> "    DECLARE V_" + columnName.trim() + StringUtil.getBlank(columnName.trim(), maxCode) + " VARCHAR(32);").collect(Collectors.joining("\n"));
        } else if (isOracle()) {
            return columnNameDeclares.stream().map(columnName -> "    V_" + columnName.trim() + StringUtil.getBlank(columnName.trim(), maxCode) + " VARCHAR2(32);").collect(Collectors.joining("\n"));
        }
        return "";
    }

    public String columnNameV() {
        String[] columnNameArr = tableSqlInfoVO.insertColumnName.split(",");
        return Arrays.stream(columnNameArr).map(columnName -> "V_" + columnName.trim()).collect(Collectors.joining(","));
    }

    public String insertColumnName() {
        return tableSqlInfoVO.insertColumnName;
    }

    public String columnNameValue() {
        String[] columnNameArr = tableSqlInfoVO.insertColumnName.split(",");
        return Arrays.stream(Arrays.copyOfRange(columnNameArr, 1, columnNameArr.length))
                .map(columnName -> "V_" + columnName.trim()).collect(Collectors.joining(", "));
    }

    public String notfound() {
        return "%NOTFOUND";
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public GeneralSqlInfoVO getGeneralSqlInfoVO() {
        return generalSqlInfoVO;
    }

    public void setGeneralSqlInfoVO(GeneralSqlInfoVO generalSqlInfoVO) {
        this.generalSqlInfoVO = generalSqlInfoVO;
    }

    public TableSqlInfoVO getTableSqlInfoVO() {
        return tableSqlInfoVO;
    }

    public void setTableSqlInfoVO(TableSqlInfoVO tableSqlInfoVO) {
        this.tableSqlInfoVO = tableSqlInfoVO;
    }

    public FieldInfoVO getFieldInfoVO() {
        return fieldInfoVO;
    }

    public void setFieldInfoVO(FieldInfoVO fieldInfoVO) {
        this.fieldInfoVO = fieldInfoVO;
    }

    public IndexInfoVO getIndexInfoVO() {
        return indexInfoVO;
    }

    public void setIndexInfoVO(IndexInfoVO indexInfoVO) {
        this.indexInfoVO = indexInfoVO;
    }

    public SqlTemplateVO getSqlTemplateVO() {
        return sqlTemplateVO;
    }

    public void setSqlTemplateVO(SqlTemplateVO sqlTemplateVO) {
        this.sqlTemplateVO = sqlTemplateVO;
    }

    public AbstractProcedureService getProcedureService() {
        return procedureService;
    }

    public void setProcedureService(AbstractProcedureService procedureService) {
        this.procedureService = procedureService;
    }
}
