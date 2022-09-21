package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class MysqlProcedureInsertSqlService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        AbstractProcedureService procedureService = sqlTemplateVO.getProcedureService();
        FileUtils.writeFile(path, procedureService.comment());
        if (!CollectionUtils.isEmpty(tableSqlInfoVO.getInsertDataInfoVOS())) {
            tableSqlInfoVO.getInsertDataInfoVOS().forEach(insertDataInfoVO -> {
                String codes = String.join(", ", insertDataInfoVO.getCodes());
                String values = String.join(", ", insertDataInfoVO.getMvalues());
                FileUtils.writeFile(path, String.format(procedureService.getProcedure(), tableSqlInfoVO.tableName, codes, values, tableSqlInfoVO.tableName, insertDataInfoVO.getIdCode(), insertDataInfoVO.getIdValue()));
            });
        }
    }
}
