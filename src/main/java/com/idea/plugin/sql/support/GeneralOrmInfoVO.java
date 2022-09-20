package com.idea.plugin.sql.support;


import com.idea.plugin.orm.support.enums.MethodEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GeneralOrmInfoVO extends GeneralInfoVO<TableOrmInfoVO> {

    public String modulePath;
    public List<String> methods = new ArrayList<>();
    public String doPath;
    public String voPath;
    public String daoPath;
    public String daoMysqlPath;
    public String daoOraclePath;
    public String servicePath;
    public String iservicePath;
    public String controllerPath;
    public String controllerReturn;

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public List<String> getMethods() {
        if (CollectionUtils.isEmpty(methods)) {
            return MethodEnum.getDefaultMthods();
        }
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public String getDoPath() {
        return doPath;
    }

    public void setDoPath(String doPath) {
        this.doPath = doPath;
    }

    public String getVoPath() {
        return voPath;
    }

    public void setVoPath(String voPath) {
        this.voPath = voPath;
    }

    public String getDaoPath() {
        return daoPath;
    }

    public void setDaoPath(String daoPath) {
        this.daoPath = daoPath;
    }

    public String getDaoMysqlPath() {
        return daoMysqlPath;
    }

    public void setDaoMysqlPath(String daoMysqlPath) {
        this.daoMysqlPath = daoMysqlPath;
    }

    public String getDaoOraclePath() {
        return daoOraclePath;
    }

    public void setDaoOraclePath(String daoOraclePath) {
        this.daoOraclePath = daoOraclePath;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public String getIservicePath() {
        return iservicePath;
    }

    public void setIservicePath(String iservicePath) {
        this.iservicePath = iservicePath;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public String getControllerReturn() {
        return controllerReturn;
    }

    public void setControllerReturn(String controllerReturn) {
        this.controllerReturn = controllerReturn.replaceAll(";", "");
    }

    public String getControllerResult() {
        if (StringUtils.isEmpty(controllerReturn)) {
            return "Result";
        }
        return controllerReturn.substring(controllerReturn.lastIndexOf(".") + 1).trim();
    }

}
