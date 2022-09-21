package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class OracleProcedureInsertSqlService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        AbstractProcedureService procedureService = sqlTemplateVO.getProcedureService();
        FileUtils.writeFile(path, procedureService.comment());
        if (!CollectionUtils.isEmpty(tableSqlInfoVO.getInsertDataInfoVOS())) {
            tableSqlInfoVO.getInsertDataInfoVOS().forEach(insertDataInfoVO -> {
                String codes = String.join(", ", insertDataInfoVO.getCodes());
                String values = String.join(", ", insertDataInfoVO.getOvalues());
                if (CollectionUtils.isNotEmpty(insertDataInfoVO.getDeclareColumns())) {
                    String declareColumn = String.join("", insertDataInfoVO.getDeclareColumns());
                    String dbmsLobCreate = String.join("", insertDataInfoVO.getDbmsLobCreates());
                    String dbmsLobApend = String.join("", insertDataInfoVO.getDbmsLobApends());
                    FileUtils.writeFile(path, String.format(procedureService.getCall(), declareColumn, dbmsLobCreate, dbmsLobApend, tableSqlInfoVO.tableName, codes, values, tableSqlInfoVO.tableName, insertDataInfoVO.getIdCode(), insertDataInfoVO.getIdValue()));
                } else {
                    FileUtils.writeFile(path, String.format(procedureService.getProcedure(), tableSqlInfoVO.tableName, codes, values, tableSqlInfoVO.tableName, insertDataInfoVO.getIdCode(), insertDataInfoVO.getIdValue()));
                }
            });
        }
    }
}
