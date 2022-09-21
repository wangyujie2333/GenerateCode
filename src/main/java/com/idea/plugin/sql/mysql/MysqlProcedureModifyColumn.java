package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;

public class MysqlProcedureModifyColumn extends AbstractProcedureService {

    public static String modifyColumnProcedure =
            "DROP PROCEDURE IF EXISTS MODIFY_${shortName}_COLUMN;\n" +
                    "DELIMITER $$\n" +
                    //ALTER TABLE T_BILL_EXPENSE_RECORD MODIFY     EXPENSE_RECORD_ID              VARCHAR(32)  NOT NULL COMMENT '主键ID';
                    "CREATE PROCEDURE MODIFY_${shortName}_COLUMN(V_TABLE_NAME TINYTEXT, V_COLUMN_NAME TINYTEXT, V_COLUMN_TYPE TINYTEXT, V_COMMENT TINYTEXT)\n" +
                    "BEGIN\n" +
                    "    BEGIN\n" +
                    "        SET @STR = concat('ALTER TABLE ', V_TABLE_NAME, '  MODIFY ', V_COLUMN_NAME, ' ', V_COLUMN_TYPE, ' COMMENT ''', V_COMMENT, '''');\n" +
                    "        SELECT count(1) INTO @CNT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = V_TABLE_NAME AND COLUMN_NAME = V_COLUMN_NAME AND TABLE_SCHEMA = DATABASE();\n" +
                    "        IF @CNT = 1 THEN PREPARE STMT FROM @STR; EXECUTE STMT; END IF;\n" +
                    "    END;\n" +
                    "END$$\n" +
                    "DELIMITER ;\n";
    public static String modifyColumnCall = "CALL MODIFY_${shortName}_COLUMN('${shortName}', '${columnName}', '${columnType} ${nullType}', '${fcomment}');\n";
    public static String modifyColumnDrop = "\nDROP PROCEDURE MODIFY_${shortName}_COLUMN;\n\n";

    @Override
    public String getProcedure() {
        if (super.getProcedure() != null) {
            return super.getProcedure();
        }
        return modifyColumnProcedure;
    }

    @Override
    public String getCall() {
        if (super.getCall() != null) {
            return super.getCall();
        }
        return modifyColumnCall;
    }

    @Override
    public String getDrop() {
        if (super.getDrop() != null) {
            return super.getDrop();
        }
        return modifyColumnDrop;
    }
}
