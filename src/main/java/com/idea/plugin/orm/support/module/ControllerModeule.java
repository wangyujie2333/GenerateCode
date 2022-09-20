package com.idea.plugin.orm.support.module;

import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.TableModule;
import org.apache.commons.lang3.StringUtils;

public class ControllerModeule extends TableModule {

    public ControllerModeule(GeneratorContext context) {
        super(context);
    }

    public String getModuleName() {
        return fileTypeInfo.getModuleName();
    }

    public String getReturn() {
        String aReturn = context.getGeneralOrmInfoVO().controllerReturn;
        if (StringUtils.isEmpty(aReturn)) {
            return "ResultValue";
        }
        return aReturn.substring(aReturn.lastIndexOf(".") + 1);
    }
}
