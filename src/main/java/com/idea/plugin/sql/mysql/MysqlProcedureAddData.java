package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;

public class MysqlProcedureAddData extends AbstractProcedureService {

    public static String insertDataProcedure =
            "DROP PROCEDURE IF EXISTS INSERT_${shortName}_DATA;\n" +
                    "DELIMITER $$\n" +
                    "CREATE PROCEDURE INSERT_${shortName}_DATA${columnParams}\n" +
                    "BEGIN\n" +
                    "${columnNameDeclare}\n" +
                    "    DECLARE DONE INT;\n" +
                    "    DECLARE CUR_TABLE_DATA_LOOP CURSOR FOR SELECT * \n" +
                    "                                           FROM ${tableName}" +
                    "${columnCondition}\n" +
                    "    DECLARE CONTINUE HANDLER FOR NOT FOUND SET DONE = 1;\n" +
                    "\n" +
                    "OPEN CUR_TABLE_DATA_LOOP;\n" +
                    "END_LEAVE:\n" +
                    "    LOOP\n" +
                    "        FETCH CUR_TABLE_DATA_LOOP INTO ${columnNameV};\n" +
                    "        IF DONE THEN LEAVE END_LEAVE; END IF;\n" +
                    "        SELECT replace(uuid(), '-', '') INTO V_TABLE_ID FROM DUAL;\n" +
                    "        INSERT INTO ${tableName} (${insertColumnName})\n" +
                    "        VALUES (V_TABLE_ID, ${columnNameValue});\n" +
                    "    END LOOP;\n" +
                    "CLOSE CUR_TABLE_DATA_LOOP;\n" +
                    "END$$\n" +
                    "DELIMITER ;\n" +
                    "\n";
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
