package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;

public class OracleProcedureAddData extends AbstractProcedureService {

    public static String insertDataProcedure =
            "CREATE OR REPLACE PROCEDURE INSERT_%s_DATA(%s) AS\n" +
                    "    V_TABLE_DATA %s;\n" +
                    "    V_TABLE_ID      VARCHAR2(32);\n" +
                    "CURSOR CUR_TABLE_DATA_LOOP IS SELECT * FROM %s\n" +
                    "                                           WHERE %s\n" +
                    "                                             AND NOT EXISTS(SELECT 1 FROM %s T WHERE T.%s);\n" +
                    "BEGIN\n" +
                    "FOR V_TABLE_DATA IN CUR_TABLE_DATA_LOOP\n" +
                    "    LOOP\n" +
                    "        SELECT lower(rawtohex(sys_guid())) INTO V_TABLE_ID FROM DUAL;\n" +
                    "        INSERT INTO %s (%s)\n" +
                    "        VALUES (V_TABLE_ID,%s);\n" +
                    "    END LOOP;\n" +
                    "COMMIT;\n" +
                    "\n" +
                    "END INSERT_%s_DATA;\n" +
                    "/\n\n";
    public static String insertDataCall = "CALL INSERT_%s_DATA();\n\n";
    public static String insertDataDrop = "DROP PROCEDURE INSERT_%s_DATA;\n\n";

    public String getComment() {
        if (super.getComment() != null) {
            return super.getComment();
        }
        return comment;
    }

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
