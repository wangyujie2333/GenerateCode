package com.idea.plugin.sql.oracle;

import com.idea.plugin.sql.BaseProcedureService;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.SqlTemplateModeule;
import com.idea.plugin.sql.support.TableSqlInfoVO;
import com.idea.plugin.sql.support.enums.PrimaryTypeEnum;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

public class OracleProcedureAddTableService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path) || CollectionUtils.isEmpty(tableSqlInfoVO.getFieldInfos())) {
            return;
        }
        OracleProcedureAddTable procedureService = new OracleProcedureAddTable();
        FileUtils.writeFile(path, procedureService.comment());
        String procedure = procedureService.getProcedure();
        Integer length = tableSqlInfoVO.getFieldInfos().stream().map(fieldInfo -> fieldInfo.columnName.length()).max(Comparator.comparing(Integer::intValue)).get();
        String call = tableSqlInfoVO.getFieldInfos().stream().map(fieldVO -> {
            String format = String.format(procedureService.getCall(), StringUtil.getBlank(fieldVO.columnName, length), fieldVO.columnType.getOtype(fieldVO.columnTypeArgs), fieldVO.nullType.getCode());
            if (PrimaryTypeEnum.PRIMARY.equals(fieldVO.primary)) {
                format = format + String.format("\n                    CONSTRAINT %s_PK PRIMARY KEY", tableSqlInfoVO.tableName);
            }
            return format;
        }).collect(Collectors.joining(",\n"));
        String callComment = tableSqlInfoVO.getFieldInfos().stream().map(fieldVO -> String.format(procedureService.getCallComment(), tableSqlInfoVO.tableName, fieldVO.columnName, fieldVO.comment)).collect(Collectors.joining("\n"));
        procedure = String.format(procedure, tableSqlInfoVO.tableName, tableSqlInfoVO.tableName, call, tableSqlInfoVO.tableName, tableSqlInfoVO.tableComment, callComment);
        FileUtils.writeFile(path, procedure);
    }

}
