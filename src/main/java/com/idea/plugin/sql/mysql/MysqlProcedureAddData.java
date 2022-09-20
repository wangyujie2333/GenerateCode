package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;

public class MysqlProcedureAddData extends AbstractProcedureService {

    public static String insertDataProcedure =
            "DROP PROCEDURE IF EXISTS INSERT_%s_DATA;\n" +
                    "DELIMITER $$\n" +
                    "CREATE PROCEDURE INSERT_%s_DATA(%s)\n" +
                    "BEGIN\n" +
                    "    DECLARE V_TABLE_ID VARCHAR(32);\n" +
                    "%s\n" +
                    "    DECLARE DONE INT;\n" +
                    "    DECLARE CUR_TABLE_DATA_LOOP CURSOR FOR SELECT * FROM %s\n" +
                    "                                           WHERE %s\n" +
                    "                                             AND NOT EXISTS(SELECT 1 FROM %s T WHERE T.%s);\n" +
                    "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET DONE = 1;\n" +
                    "\n" +
                    "OPEN CUR_TABLE_DATA_LOOP;\n" +
                    "END_LEAVE:\n" +
                    "    LOOP\n" +
                    "        FETCH CUR_TABLE_DATA_LOOP INTO %s;\n" +
                    "        IF DONE THEN LEAVE END_LEAVE; END IF;\n" +
                    "        SELECT replace(uuid(), '-', '') INTO V_TABLE_ID FROM DUAL;\n" +
                    "        INSERT INTO %s (%s)\n" +
                    "        VALUES (V_TABLE_ID,%s);\n" +
                    "    END LOOP;\n" +
                    "CLOSE CUR_TABLE_DATA_LOOP;\n" +
                    "END$$\n" +
                    "DELIMITER ;\n" +
                    "\n";
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
