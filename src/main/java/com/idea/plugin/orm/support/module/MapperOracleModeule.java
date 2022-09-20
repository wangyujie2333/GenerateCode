package com.idea.plugin.orm.support.module;

import com.idea.plugin.orm.service.GeneratorXmlFileStrService;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.sql.support.enums.DataTypeEnum;

public class MapperOracleModeule extends MapperModeule {

    public MapperOracleModeule(GeneratorContext context) {
        super(context);
    }

    public String getMethodsStr() {
        return GeneratorXmlFileStrService.getXmlBatchMethodStr(context, DataTypeEnum.ORACLE.name());
    }
}
