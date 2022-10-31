package com.idea.plugin.sql.mysql;

import com.idea.plugin.sql.AbstractProcedureService;
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

public class MysqlProcedureAddTableService extends BaseProcedureService {

    public void addProcedure(String path, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, SqlTemplateModeule sqlTemplateVO) {
        if (StringUtils.isEmpty(path) || CollectionUtils.isEmpty(tableSqlInfoVO.getFieldInfos())) {
            return;
        }
        AbstractProcedureService procedureService = sqlTemplateVO.getProcedureService();
        FileUtils.writeFile(path, procedureService.comment());
        String procedure = procedureService.getProcedure();
        Integer length = tableSqlInfoVO.getFieldInfos().stream().map(fieldInfo -> fieldInfo.columnName.length()).max(Comparator.comparing(Integer::intValue)).get();
        String call = tableSqlInfoVO.getFieldInfos().stream().map(fieldVO -> {
                    String format = String.format(procedureService.getCall(), fieldVO.columnName + StringUtil.getBlank(fieldVO.columnName, length), fieldVO.columnType.getMtype(fieldVO.columnTypeArgs), fieldVO.nullType.getCode(), fieldVO.comment);
                    if (PrimaryTypeEnum.PRIMARY.equals(fieldVO.primary)) {
                        format = format + fieldVO.primary.getCode();
                    }
                    return format;
                })
                .collect(Collectors.joining(",\n"));
        procedure = String.format(procedure, tableSqlInfoVO.tableName, call, tableSqlInfoVO.tableComment);
        FileUtils.writeFile(path, procedure);
    }

}
