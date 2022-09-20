package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;

public class OracleProcedureModifyColumn extends AbstractProcedureService {

    public static String modifyColumnProcedure =
            "CREATE OR REPLACE PROCEDURE MODIFY_${shortName}_COLUMN(V_TABLE_NAME IN VARCHAR, V_COLUMN_NAME IN VARCHAR, V_COLUMN_TYPE IN VARCHAR, V_COMMENT IN VARCHAR) AS\n" +
                    "    V_T_COUNT NUMBER;\n" +
                    "BEGIN\n" +
                    "    SELECT count(1) INTO V_T_COUNT FROM USER_TAB_COLUMNS WHERE TABLE_NAME = V_TABLE_NAME AND COLUMN_NAME = V_COLUMN_NAME;\n" +
                    "    IF V_T_COUNT = 1 THEN\n" +
                    "        EXECUTE IMMEDIATE 'ALTER TABLE ' || V_TABLE_NAME || ' MODIFY ' || V_COLUMN_NAME || ' ' || V_COLUMN_TYPE;\n" +
                    "        EXECUTE IMMEDIATE 'COMMENT ON COLUMN ' || V_TABLE_NAME || '.' || V_COLUMN_NAME || ' IS ''' || V_COMMENT || '''';\n" +
                    "    END IF;\n" +
                    "END ;\n" +
                    "/\n";
    public static String modifyColumnCall = "CALL MODIFY_${shortName}_COLUMN('${shortName}', '${columnName}', '${columnType} ${nullType}', '${fcomment}');\n";
    public static String modifyColumnDrop = "\nDROP PROCEDURE MODIFY_${shortName}_COLUMN;\n\n";

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
