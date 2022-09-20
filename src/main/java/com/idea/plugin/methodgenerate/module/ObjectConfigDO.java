package com.idea.plugin.methodgenerate.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectConfigDO {

    private String clazzName;
    private String clazzParamName;
    private List<String> paramList = new ArrayList<>();
    private Map<String, String> paramSetMtdMap = new HashMap<>();
    private Map<String, String> paramGetMtdMap = new HashMap<>();

    public ObjectConfigDO(String clazzName, String clazzParamName) {
        this.clazzName = clazzName;
        this.clazzParamName = clazzParamName;
    }

    public void setParam(String param) {
        this.paramList.add(param);
        this.paramSetMtdMap.put(param, "set" + param.substring(0, 1).toUpperCase() + param.substring(1));
        this.paramGetMtdMap.put(param, "get" + param.substring(0, 1).toUpperCase() + param.substring(1));
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getClazzParamName() {
        return clazzParamName;
    }

    public void setClazzParamName(String clazzParamName) {
        this.clazzParamName = clazzParamName;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }

    public Map<String, String> getParamSetMtdMap() {
        return paramSetMtdMap;
    }

    public void setParamSetMtdMap(Map<String, String> paramSetMtdMap) {
        this.paramSetMtdMap = paramSetMtdMap;
    }

    public Map<String, String> getParamGetMtdMap() {
        return paramGetMtdMap;
    }

    public void setParamGetMtdMap(Map<String, String> paramGetMtdMap) {
        this.paramGetMtdMap = paramGetMtdMap;
    }
}
