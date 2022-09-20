package com.idea.plugin.sql;

import com.idea.plugin.setting.template.SqlTemplateVO;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.sql.support.enums.DataProcedureTypeEnum;
import com.idea.plugin.sql.support.enums.DataTypeEnum;

public abstract class AbstractProcedureService implements IProcedureService {

    public final static SqlTemplateModeule sqlTemplateModeule = new SqlTemplateModeule();

    public static String comment = "${comment}";


    public static SqlTemplateModeule getSqlTemplateVO(GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO) {
        sqlTemplateModeule.setGeneralSqlInfoVO(generalSqlInfoVO);
        sqlTemplateModeule.setTableSqlInfoVO(tableSqlInfoVO);
        return sqlTemplateModeule;
    }

    public static BaseProcedureService getBaseProcedureService(GeneralSqlInfoVO generalSqlInfoVO, DataTypeEnum dataTypeEnum) {
        return DataProcedureTypeEnum.getProcedureService(generalSqlInfoVO.getProcedureTypeEnum(), dataTypeEnum);
    }

    public static AbstractProcedureService getAbstractProcedureService(GeneralSqlInfoVO generalSqlInfoVO, DataTypeEnum dataTypeEnum) {
        return DataProcedureTypeEnum.getAbstractProcedureService(generalSqlInfoVO.getProcedureTypeEnum(), dataTypeEnum);
    }


    public String comment() {
        return sqlTemplateModeule.getTemplate(getComment());
    }

    public String procedure() {
        return sqlTemplateModeule.getTemplate(getProcedure());
    }

    public String call() {
        return sqlTemplateModeule.getTemplate(getCall());
    }

    public String drop() {
        return sqlTemplateModeule.getTemplate(getDrop());
    }

    @Override
    public String getComment() {
        if (sqlTemplateModeule.getSqlTemplate().isPresent()) {
            return sqlTemplateModeule.getSqlTemplate().get().getComment();
        }
        return null;
    }

    @Override
    public String getProcedure() {
        SqlTemplateVO.SqlStr sqlStr = getSqlStr();
        if (sqlStr != null) {
            return sqlStr.getProcedure();
        }
        return null;
    }


    @Override
    public String getCall() {
        SqlTemplateVO.SqlStr sqlStr = getSqlStr();
        if (sqlStr != null) {
            return sqlStr.getCall();
        }
        return null;
    }

    @Override
    public String getDrop() {
        SqlTemplateVO.SqlStr sqlStr = getSqlStr();
        if (sqlStr != null) {
            return sqlStr.getDrop();
        }
        return null;
    }


    private SqlTemplateVO.SqlStr getSqlStr() {
        SqlTemplateVO.SqlStr sqlStr = null;
        if (sqlTemplateModeule.getSqlTemplate().isPresent()) {
            if (DataTypeEnum.MYSQL.equals(sqlTemplateModeule.getGeneralSqlInfoVO().getDataType())) {
                sqlStr = sqlTemplateModeule.getSqlTemplate().get().getMysql();
            } else if (DataTypeEnum.ORACLE.equals(sqlTemplateModeule.getGeneralSqlInfoVO().getDataType())) {
                sqlStr = sqlTemplateModeule.getSqlTemplate().get().getOracle();
            }
        }
        return sqlStr;
    }
}
