package com.idea.plugin.orm.support.module;

import com.idea.plugin.orm.service.GeneratorXmlFileStrService;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.sql.support.enums.DataTypeEnum;

public class MapperMysqlModeule extends MapperModeule {

    public MapperMysqlModeule(GeneratorContext context) {
        super(context);
    }

    public String getMethodsStr() {
        return GeneratorXmlFileStrService.getXmlBatchMethodStr(context, DataTypeEnum.MYSQL.name());
    }
}
