package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.FieldInfoVO;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class OracleProcedureAddColumnService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path) || CollectionUtils.isEmpty(tableSqlInfoVO.getFieldInfos())) {
            return;
        }
        AbstractProcedureService procedureService = sqlTemplateVO.getProcedureService();
        if (sqlTemplateVO.isMerge()) {
            if (tableSqlInfoVO.getIsMergeAddColumnStart()) {
                FileUtils.writeFile(path, procedureService.comment());
                FileUtils.writeFile(path, procedureService.procedure());
            }
            FileUtils.writeFile(path, procedureService.comment());
        } else {
            FileUtils.writeFile(path, procedureService.comment());
            FileUtils.writeFile(path, procedureService.procedure());
        }
        for (FieldInfoVO fieldVO : tableSqlInfoVO.getFieldInfos()) {
            sqlTemplateVO.setFieldInfoVO(fieldVO);
            FileUtils.writeFile(path, procedureService.call());
        }
        if (tableSqlInfoVO.getIsMergeAddColumnEnd()) {
            FileUtils.writeFile(path, procedureService.drop());
        }
    }
}
