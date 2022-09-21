package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;

public class OracleProcedureAddIndex extends AbstractProcedureService {

    public static String addIndexProcedure =
            "CREATE OR REPLACE PROCEDURE ADD_${shortName}_INDEX(V_TABLE_NAME IN VARCHAR, V_INDEX_NAME IN VARCHAR, V_COLUMN_NAME IN VARCHAR) AS\n" +
                    "    V_T_COUNT NUMBER;\n" +
                    "BEGIN\n" +
                    "    SELECT count(1) INTO V_T_COUNT FROM USER_INDEXES WHERE TABLE_NAME = V_TABLE_NAME AND INDEX_NAME = V_INDEX_NAME;\n" +
                    "    IF V_T_COUNT = 0 THEN EXECUTE IMMEDIATE 'CREATE INDEX ' || V_INDEX_NAME || ' ON ' || V_TABLE_NAME || ' (' || V_COLUMN_NAME || ')'; END IF;\n" +
                    "END ;\n" +
                    "/\n";
    public static String addIndexCall = "CALL ADD_${shortName}_INDEX('${tableName}', '${indexName}', '${indexColumnName}');\n";
    public static String addIndexDrop = "\nDROP PROCEDURE ADD_${shortName}_INDEX;\n\n";

    @Override
    public String getProcedure() {
        if (super.getProcedure() != null) {
            return super.getProcedure();
        }
        return addIndexProcedure;
    }

    @Override
    public String getCall() {
        if (super.getCall() != null) {
            return super.getCall();
        }
        return addIndexCall;
    }

    @Override
    public String getDrop() {
        if (super.getDrop() != null) {
            return super.getDrop();
        }
        return addIndexDrop;
    }
}
