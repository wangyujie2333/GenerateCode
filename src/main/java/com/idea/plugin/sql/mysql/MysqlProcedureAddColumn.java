package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;

public class MysqlProcedureAddColumn extends AbstractProcedureService {

    public static String addColumnProcedure =
            "DROP PROCEDURE IF EXISTS ADD_${shortName}_COLUMN;\n" +
                    "DELIMITER $$\n" +
                    "CREATE PROCEDURE ADD_${shortName}_COLUMN(V_TABLE_NAME TINYTEXT, V_COLUMN_NAME TINYTEXT, V_COLUMN_TYPE TINYTEXT, V_COMMENT TINYTEXT)\n" +
                    "BEGIN\n" +
                    "    BEGIN\n" +
                    "        SET @STR = concat('ALTER TABLE ', V_TABLE_NAME, '  ADD ', V_COLUMN_NAME, ' ', V_COLUMN_TYPE, ' COMMENT ''', V_COMMENT, '''');\n" +
                    "        SELECT count(1) INTO @CNT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = V_TABLE_NAME AND COLUMN_NAME = V_COLUMN_NAME AND TABLE_SCHEMA = DATABASE();\n" +
                    "        IF @CNT = 0 THEN PREPARE STMT FROM @STR; EXECUTE STMT; END IF;\n" +
                    "    END;\n" +
                    "END$$\n" +
                    "DELIMITER ;\n";
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
