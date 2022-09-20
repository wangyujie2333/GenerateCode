package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.FileUtils;

public class OracleProcedureInitialService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        FileUtils.writeFile(path, "-- " + (generalSqlInfoVO.author == null ? "" : generalSqlInfoVO.author) + " " + generalSqlInfoVO.fileName + "\n");
    }
}
