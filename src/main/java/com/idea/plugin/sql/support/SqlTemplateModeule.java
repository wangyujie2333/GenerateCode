package com.idea.plugin.sql.support;

import com.idea.plugin.report.support.TemplateModule;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.template.SqlTemplateVO;
import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.support.enums.DataTypeEnum;
import com.idea.plugin.sql.support.enums.ProcedureTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

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
