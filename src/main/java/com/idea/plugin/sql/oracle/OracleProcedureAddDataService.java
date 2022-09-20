package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OracleProcedureAddDataService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        AbstractProcedureService procedureService =sqlTemplateVO.getProcedureService();
        FileUtils.writeFile(path, procedureService.comment());
        String[] columnNameArr = tableSqlInfoVO.insertColumnName.split(",");
        String columnNameValue = Arrays.stream(Arrays.copyOfRange(columnNameArr, 1, columnNameArr.length))
                .map(columnName -> "V_TABLE_DATA." + columnName.trim()).collect(Collectors.joining(", "));
        String columnParams = "P_PARAM  IN VARCHAR";
        String columnCondition = "PARAM = P_PARAM";
        if (tableSqlInfoVO.insertColumnParam != null) {
            String[] columnParamArr = tableSqlInfoVO.insertColumnParam.split(",");
            columnParams = Arrays.stream(columnParamArr)
                    .map(columnParam -> "P_" + columnParam.trim() + " IN VARCHAR").collect(Collectors.joining(", "));
            columnCondition = columnParamArr[0].trim() + " = " + "P_" + columnParamArr[0].trim();
        }
        String procedure = String.format(procedureService.getProcedure(),
                tableSqlInfoVO.tableShortName(), columnParams, tableSqlInfoVO.tableName + "%ROWTYPE",
                tableSqlInfoVO.tableName, columnCondition, tableSqlInfoVO.tableName, columnCondition,
                tableSqlInfoVO.tableName, tableSqlInfoVO.insertColumnName, columnNameValue, tableSqlInfoVO.tableName);
        FileUtils.writeFile(path, procedure);
        FileUtils.writeFile(path, String.format(procedureService.getCall(), tableSqlInfoVO.tableShortName()));
        FileUtils.writeFile(path, String.format(procedureService.getDrop(), tableSqlInfoVO.tableShortName()));
    }
}
