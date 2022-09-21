package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OracleProcedureAddDataService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        AbstractProcedureService procedureService = sqlTemplateVO.getProcedureService();
        FileUtils.writeFile(path, procedureService.comment());
        FileUtils.writeFile(path, procedureService.procedure());
        FileUtils.writeFile(path, procedureService.call());
        FileUtils.writeFile(path, procedureService.drop());
    }

}
