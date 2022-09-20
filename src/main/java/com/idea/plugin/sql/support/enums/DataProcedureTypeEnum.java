package com.idea.plugin.sql.support.enums;

import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.mysql.*;
import com.idea.plugin.sql.oracle.*;

import java.util.Arrays;

public enum DataProcedureTypeEnum {
    MYSQL_INITIAL(ProcedureTypeEnum.INITIAL, DataTypeEnum.MYSQL, null, new MysqlProcedureInitialService()),
    MYSQL_ADD_TABLE(ProcedureTypeEnum.ADD_TABLE, DataTypeEnum.MYSQL, new MysqlProcedureAddTable(), new MysqlProcedureAddTableService()),
    MYSQL_ADD_INDEX(ProcedureTypeEnum.ADD_INDEX, DataTypeEnum.MYSQL, new MysqlProcedureAddIndex(), new MysqlProcedureAddIndexService()),
    MYSQL_MODIFY_COLUMN(ProcedureTypeEnum.MODIFY_COLUMN, DataTypeEnum.MYSQL, new MysqlProcedureModifyColumn(), new MysqlProcedureModifyColumnService()),
    MYSQL_ADD_COLUMN(ProcedureTypeEnum.ADD_COLUMN, DataTypeEnum.MYSQL, new MysqlProcedureAddColumn(), new MysqlProcedureAddColumnService()),
    MYSQL_ADD_DATA(ProcedureTypeEnum.ADD_DATA, DataTypeEnum.MYSQL, new MysqlProcedureAddData(), new MysqlProcedureAddDataService()),
    MYSQL_INSERT_DATA(ProcedureTypeEnum.INSERT_DATA, DataTypeEnum.MYSQL, new MysqlProcedureInsertData(), new MysqlProcedureInsertDataService()),
    MYSQL_INSERT_SQL(ProcedureTypeEnum.INSERT_SQL, DataTypeEnum.MYSQL, new MysqlProcedureInsertData(), new MysqlProcedureInsertSqlService()),
    ORACLE_INITIAL(ProcedureTypeEnum.INITIAL, DataTypeEnum.ORACLE, null, new OracleProcedureInitialService()),
    ORACLE_ADD_TABLE(ProcedureTypeEnum.ADD_TABLE, DataTypeEnum.ORACLE, new OracleProcedureAddTable(), new OracleProcedureAddTableService()),
    ORACLE_ADD_INDEX(ProcedureTypeEnum.ADD_INDEX, DataTypeEnum.ORACLE, new OracleProcedureAddIndex(), new OracleProcedureAddIndexService()),
    ORACLE_MODIFY_COLUMN(ProcedureTypeEnum.MODIFY_COLUMN, DataTypeEnum.ORACLE, new OracleProcedureModifyColumn(), new OracleProcedureModifyColumnService()),
    ORACLE_ADD_COLUMN(ProcedureTypeEnum.ADD_COLUMN, DataTypeEnum.ORACLE, new OracleProcedureAddColumn(), new OracleProcedureAddColumnService()),
    ORACLE_ADD_DATA(ProcedureTypeEnum.ADD_DATA, DataTypeEnum.ORACLE, new OracleProcedureAddData(), new OracleProcedureAddDataService()),
    ORACLE_INSERT_DATA(ProcedureTypeEnum.INSERT_DATA, DataTypeEnum.ORACLE, new OracleProcedureInsertData(), new OracleProcedureInsertDataService()),
    ORACLE_INSERT_SQL(ProcedureTypeEnum.INSERT_SQL, DataTypeEnum.ORACLE, new OracleProcedureInsertData(), new OracleProcedureInsertSqlService()),
    ;


    private ProcedureTypeEnum procedureTypeEnum;
    private DataTypeEnum dataTypeEnum;
    private AbstractProcedureService abstractProcedureService;
    private BaseProcedureService procedureService;

    DataProcedureTypeEnum(ProcedureTypeEnum procedureTypeEnum, DataTypeEnum dataTypeEnum, AbstractProcedureService abstractProcedureService, BaseProcedureService procedureService) {
        this.procedureTypeEnum = procedureTypeEnum;
        this.dataTypeEnum = dataTypeEnum;
        this.abstractProcedureService = abstractProcedureService;
        this.procedureService = procedureService;
    }

    public ProcedureTypeEnum getProcedureTypeEnum() {
        return procedureTypeEnum;
    }

    public DataTypeEnum getDataTypeEnum() {
        return dataTypeEnum;
    }

    public BaseProcedureService getProcedureService() {
        return procedureService;
    }

    public AbstractProcedureService getAbstractProcedureService() {
        return abstractProcedureService;
    }

    public static BaseProcedureService getProcedureService(ProcedureTypeEnum procedureTypeEnum, DataTypeEnum dataTypeEnum) {
        return Arrays.stream(DataProcedureTypeEnum.values()).filter(dataProcedureTypeEnum ->
                        dataProcedureTypeEnum.getDataTypeEnum().equals(dataTypeEnum)
                                && dataProcedureTypeEnum.getProcedureTypeEnum().equals(procedureTypeEnum))
                .findAny().map(DataProcedureTypeEnum::getProcedureService).orElse(null);
    }

    public static AbstractProcedureService getAbstractProcedureService(ProcedureTypeEnum procedureTypeEnum, DataTypeEnum dataTypeEnum) {
        return Arrays.stream(DataProcedureTypeEnum.values()).filter(dataProcedureTypeEnum ->
                        dataProcedureTypeEnum.getDataTypeEnum().equals(dataTypeEnum)
                                && dataProcedureTypeEnum.getProcedureTypeEnum().equals(procedureTypeEnum))
                .findAny().map(DataProcedureTypeEnum::getAbstractProcedureService).orElse(null);
    }
}
