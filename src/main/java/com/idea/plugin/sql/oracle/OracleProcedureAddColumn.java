package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;

public class OracleProcedureAddColumn extends AbstractProcedureService {

    public static String addColumnProcedure =
            "CREATE OR REPLACE PROCEDURE ADD_${shortName}_COLUMN(V_TABLE_NAME IN VARCHAR, V_COLUMN_NAME IN VARCHAR, V_COLUMN_TYPE IN VARCHAR, V_COMMENT IN VARCHAR) AS\n" +
                    "    V_T_COUNT NUMBER;\n" +
                    "BEGIN\n" +
                    "    SELECT count(1) INTO V_T_COUNT FROM USER_TAB_COLUMNS WHERE TABLE_NAME = V_TABLE_NAME AND COLUMN_NAME = V_COLUMN_NAME;\n" +
                    "    IF V_T_COUNT = 0 THEN\n" +
                    "        EXECUTE IMMEDIATE 'ALTER TABLE ' || V_TABLE_NAME || ' ADD ' || V_COLUMN_NAME || ' ' || V_COLUMN_TYPE;\n" +
                    "        EXECUTE IMMEDIATE 'COMMENT ON COLUMN ' || V_TABLE_NAME || '.' || V_COLUMN_NAME || ' IS ''' || V_COMMENT || '''';\n" +
                    "    END IF;\n" +
                    "END ;\n" +
                    "/\n";
    public static String addColumnCall = "CALL ADD_${shortName}_COLUMN('${tableName}', '${columnName}', '${columnType} ${nullType}', '${fcomment}');\n";
    public static String addColumnDrop = "\nDROP PROCEDURE ADD_${shortName}_COLUMN;\n\n";

    @Override
    public String getProcedure() {
        if (super.getProcedure() != null) {
            return super.getProcedure();
        }
        return addColumnProcedure;
    }

    @Override
    public String getCall() {
        if (super.getCall() != null) {
            return super.getCall();
        }
        return addColumnCall;
    }

    @Override
    public String getDrop() {
        if (super.getDrop() != null) {
            return super.getDrop();
        }
        return addColumnDrop;
    }
}
