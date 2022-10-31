package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;

public class OracleProcedureAddData extends AbstractProcedureService {

    public static String insertDataProcedure =
            "CREATE OR REPLACE PROCEDURE INSERT_${shortName}_DATA${columnParams}\n" +
                    "${columnNameDeclare}\n" +
                    "CURSOR CUR_TABLE_DATA_LOOP IS SELECT * \n" +
                    "                              FROM ${tableName}${columnCondition}\n" +
                    "BEGIN\n" +
                    "OPEN CUR_TABLE_DATA_LOOP;\n" +
                    "    LOOP\n" +
                    "        FETCH CUR_TABLE_DATA_LOOP INTO ${columnNameV};\n" +
                    "        EXIT WHEN CUR_TABLE_DATA_LOOP${notfound};\n" +
                    "        SELECT lower(rawtohex(sys_guid())) INTO V_TABLE_ID FROM DUAL;\n" +
                    "        INSERT INTO ${tableName} (${insertColumnName})\n" +
                    "        VALUES (V_TABLE_ID, ${columnNameValue});\n" +
                    "    END LOOP;\n" +
                    "COMMIT;\n" +
                    "\n" +
                    "END INSERT_${shortName}_DATA;\n" +
                    "/\n\n";
    public static String insertDataCall = "CALL INSERT_${shortName}_DATA(${param});\n\n";
    public static String insertDataDrop = "DROP PROCEDURE INSERT_${shortName}_DATA;\n\n";

    @Override
    public String getProcedure() {
        if (super.getProcedure() != null) {
            return super.getProcedure();
        }
        return insertDataProcedure;
    }

    @Override
    public String getCall() {
        if (super.getCall() != null) {
            return super.getCall();
        }
        return insertDataCall;
    }

    @Override
    public String getDrop() {
        if (super.getDrop() != null) {
            return super.getDrop();
        }
        return insertDataDrop;
    }
}
