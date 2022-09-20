package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;

public class OracleProcedureInsertData extends AbstractProcedureService {

    public static String insertDataProcedure =
            "INSERT INTO %s(%s)\n" +
                    "SELECT %s FROM DUAL\n" +
                    "WHERE NOT exists(SELECT 1 FROM %s T WHERE T.%s = %s);\n";

    public static String insertDataCall =
            "DECLARE\n" +
                    "%s" +
                    "BEGIN\n" +
                    "%s" +
                    "%s" +
                    "    INSERT INTO %s(%s)\n" +
                    "    SELECT %s FROM DUAL\n" +
                    "    WHERE NOT exists(SELECT 1 FROM %s T WHERE T.%s = %s);\n" +
                    "END ;\n" +
                    "/\n";

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
        return null;
    }
}
