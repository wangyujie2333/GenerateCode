package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;

public class MysqlProcedureAddIndex extends AbstractProcedureService {

    public static String addIndexProcedure =
            "DROP PROCEDURE IF EXISTS ADD_${shortName}_INDEX;\n" +
                    "DELIMITER $$\n" +
                    "CREATE PROCEDURE ADD_${shortName}_INDEX(V_TABLE_NAME TINYTEXT, V_INDEX_NAME TINYTEXT, V_COLUMN_NAME TINYTEXT)\n" +
                    "BEGIN\n" +
                    "    BEGIN\n" +
                    "        SET @STR = concat('CREATE INDEX ', V_INDEX_NAME, ' ON ', V_TABLE_NAME, ' (', V_COLUMN_NAME, ')');\n" +
                    "        SELECT count(1) INTO @CNT FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME = V_TABLE_NAME AND INDEX_NAME = V_INDEX_NAME AND TABLE_SCHEMA = DATABASE();\n" +
                    "        IF @CNT = 0 THEN PREPARE STMT FROM @STR; EXECUTE STMT; END IF;\n" +
                    "    END;\n" +
                    "END$$\n" +
                    "DELIMITER ;\n";
    public static String addIndexCall = "CALL ADD_${shortName}_INDEX('${tableName}', '${indexName}', '${indexColumnName}');\n";
    public static String addIndexDrop = "\nDROP PROCEDURE ADD_${shortName}_INDEX;\n\n";

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
