package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.DBUtils;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class OracleProcedureInsertDataService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path) || CollectionUtils.isEmpty(tableSqlInfoVO.insertData)) {
            return;
        }
        AbstractProcedureService procedureService = sqlTemplateVO.getProcedureService();
        FileUtils.writeFile(path, procedureService.comment());
        Pattern pattern = Pattern.compile("^'\\d{4}-\\d{2}-\\d{2}");
        for (String insertSql : tableSqlInfoVO.insertData) {
            int s1 = insertSql.indexOf("(");
            int s2 = insertSql.indexOf(")");
            int s3 = insertSql.lastIndexOf("(");
            int s4 = insertSql.lastIndexOf(")");
            String codes = insertSql.substring(s1 + 1, s2).toUpperCase();
            String values = insertSql.substring(s3 + 1, s4);
            String[] codeArr = codes.split(",");
            String idCode = codeArr[0];
            String[] valuesArr = values.split(",");
            String idValue = DBUtils.getIdValue(valuesArr[0]);
            List<String> valueList = new ArrayList<>();
            valueList.add(idValue);
            for (int i = 1; i < valuesArr.length; i++) {
                String value = valuesArr[i];
                value = value.trim();
                if (pattern.matcher(value).find()) {
                    if ("SYSDATE".equalsIgnoreCase(value) || "CREATE_DATE".equals(codeArr[i]) || "UPDATE_DATE".equals(codeArr[i])) {
                        valueList.add("SYSDATE");
                    } else {
                        valueList.add("TIMESTAMP " + value);
                    }
                } else {
                    valueList.add(value);
                }
            }
            values = String.join(", ", valueList);
            FileUtils.writeFile(path, String.format(procedureService.getProcedure(), tableSqlInfoVO.tableName, codes, values, tableSqlInfoVO.tableName, idCode, idValue));
        }
    }
}
