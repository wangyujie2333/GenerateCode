package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;

public class OracleProcedureAddTable extends AbstractProcedureService {

    public static String addTableProcedure =
            "DECLARE\n" +
                    "    V_T_COUNT           NUMBER;\n" +
                    "BEGIN\n" +
                    "    SELECT count(1) INTO V_T_COUNT FROM USER_TABLES WHERE TABLE_NAME = '%s';\n" +
                    "    IF V_T_COUNT = 0 THEN\n" +
                    "        EXECUTE IMMEDIATE  'CREATE TABLE %s\n" +
                    "            (\n" +
                    "%s" +
                    "            \n)';\n" +
                    "        EXECUTE IMMEDIATE  'COMMENT ON TABLE %s IS ''%s''';\n" +
                    "%s\n" +
                    "    END IF;\n" +
                    "END ;\n" +
                    "/\n";
    public static String addTableCall = "                %s %s%s";
    public static String addTableCallComment = "        EXECUTE IMMEDIATE  'COMMENT ON COLUMN %s.%s IS ''%s''';";

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
        return addTableProcedure;
    }

    @Override
    public String getCall() {
        if (super.getCall() != null) {
            return super.getCall();
        }
        return addTableCall;
    }

    public String getCallComment() {
        return addTableCallComment;
    }

    @Override
    public String getDrop() {
        if (super.getDrop() != null) {
            return super.getDrop();
        }
        return null;
    }
}
