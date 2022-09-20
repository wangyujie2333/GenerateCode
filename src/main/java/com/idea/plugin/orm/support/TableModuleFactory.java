package com.idea.plugin.orm.support;

import com.idea.plugin.orm.support.module.*;

public class TableModuleFactory {
    public static void createTableModule(GeneratorContext context) {
        TableModule tableModule = null;
        switch (context.getFileType()) {
            case DO:
                tableModule = new EntityModeule(context);
                break;
            case VO:
                tableModule = new VoModeule(context);
                break;
            case DAO:
                tableModule = new DaoModeule(context);
                break;
            case MAPPER:
                tableModule = new MapperModeule(context);
                break;
            case MAPPER_MYSQL:
                tableModule = new MapperMysqlModeule(context);
                break;
            case MAPPER_ORACLE:
                tableModule = new MapperOracleModeule(context);
                break;
            case SERVICE:
                tableModule = new ServiceModeule(context);
                break;
            case ISERVICE:
                tableModule = new IServiceModeule(context);
                break;
            case CONTROLLER:
                tableModule = new ControllerModeule(context);
                break;
            default:
                break;
        }
        context.setTableModule(tableModule);
    }
}