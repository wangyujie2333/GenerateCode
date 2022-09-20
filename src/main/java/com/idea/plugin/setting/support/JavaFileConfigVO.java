package com.idea.plugin.setting.support;

import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.orm.support.enums.MethodEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class JavaFileConfigVO extends BaseConfigVO {
    public boolean DO;
    public boolean DAO;
    public boolean SERVICE;
    public boolean CONTROLLER;
    public String modulePath;
    public List<String> methods = new ArrayList<>();
    public String doPath;
    public String voPath;
    public String daoPath;
    public String iservicePath;
    public String servicePath;
    public String controllerPath;
    public String controllerReturn;

    public void copy(TableConfigVO configVO) {
        setDO(configVO.getProcedureTypeList().stream().anyMatch(procedureType -> FileTypePathEnum.DO.name().equals(procedureType)));
        setDAO(configVO.getProcedureTypeList().stream().anyMatch(procedureType -> FileTypePathEnum.DAO.name().equals(procedureType)));
        setSERVICE(configVO.getProcedureTypeList().stream().anyMatch(procedureType -> FileTypePathEnum.SERVICE.name().equals(procedureType)));
        setCONTROLLER(configVO.getProcedureTypeList().stream().anyMatch(procedureType -> FileTypePathEnum.CONTROLLER.name().equals(procedureType)));
    }

    public boolean isDO() {
        return DO;
    }

    public void setDO(boolean DO) {
        this.DO = DO;
    }

    public boolean isDAO() {
        return DAO;
    }

    public void setDAO(boolean DAO) {
        this.DAO = DAO;
    }

    public boolean isSERVICE() {
        return SERVICE;
    }

    public void setSERVICE(boolean SERVICE) {
        this.SERVICE = SERVICE;
    }

    public boolean isCONTROLLER() {
        return CONTROLLER;
    }

    public void setCONTROLLER(boolean CONTROLLER) {
        this.CONTROLLER = CONTROLLER;
    }

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

    public String getIservicePath() {
        return iservicePath;
    }

    public void setIservicePath(String iservicePath) {
        this.iservicePath = iservicePath;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
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
        this.controllerReturn = controllerReturn;
    }
}
