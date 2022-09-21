package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.IndexInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class MysqlProcedureAddIndexService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path) || CollectionUtils.isEmpty(tableSqlInfoVO.getIndexInfos())) {
            return;
        }
        AbstractProcedureService procedureService = sqlTemplateVO.getProcedureService();
        if (sqlTemplateVO.isMerge()) {
            if (tableSqlInfoVO.getIsMergeAddIndexStart()) {
                FileUtils.writeFile(path, procedureService.getPComment(generalSqlInfoVO));
                FileUtils.writeFile(path, procedureService.procedure());
            }
            FileUtils.writeFile(path, procedureService.comment());
        } else {
            FileUtils.writeFile(path, procedureService.comment());
            FileUtils.writeFile(path, procedureService.procedure());
        }
        for (IndexInfoVO indexInfoVO : tableSqlInfoVO.getIndexInfos()) {
            sqlTemplateVO.setIndexInfoVO(indexInfoVO);
            FileUtils.writeFile(path, procedureService.call());
        }
        if (tableSqlInfoVO.getIsMergeAddIndexEnd()) {
            FileUtils.writeFile(path, procedureService.drop());
        }
    }
}
