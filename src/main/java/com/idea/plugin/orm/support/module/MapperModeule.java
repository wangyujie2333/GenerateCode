package com.idea.plugin.orm.support.module;

import com.idea.plugin.orm.service.GeneratorXmlFileStrService;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.TableModule;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;

import java.util.List;
import java.util.stream.Collectors;

public class MapperModeule extends TableModule {

    public MapperModeule(GeneratorContext context) {
        super(context);
    }

    public String getEntityNamePath() {
        return (FileTypePathEnum.DO.getJavapath(context.getGeneralOrmInfoVO()) + "/" + FileTypePathEnum.DO.getFileName(tableInfoVO.tableName)).replaceAll("/", ".");
    }

    public String getMapperNamePath() {
        return (FileTypePathEnum.MAPPER.getJavapath(context.getGeneralOrmInfoVO()) + "/" + FileTypePathEnum.MAPPER.getFileName(tableInfoVO.tableName)).replaceAll("/", ".");
    }

    public String getMethodsStr() {
        List<String> methods = context.getGeneralOrmInfoVO().getMethods().stream().filter(s -> !s.contains("Batch")).collect(Collectors.toList());
        return GeneratorXmlFileStrService.getXmlMethodStr(context, methods);
    }

}
