package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;

public class MysqlProcedureAddTable extends AbstractProcedureService {

    public static String addTableProcedure =
            "CREATE TABLE IF NOT EXISTS %s\n" +
                    "(\n" +
                    "%s\n" +
                    ") COMMENT '%s';\n" +
                    "\n";
    public static String addTableCall = "    %s %s %s COMMENT '%s'";

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

    @Override
    public String getDrop() {
        if (super.getDrop() != null) {
            return super.getDrop();
        }
        return null;
    }
}
