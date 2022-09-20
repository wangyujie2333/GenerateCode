package com.idea.plugin.sql.support;

import java.util.ArrayList;
import java.util.List;

public class InsertDataInfoVO {
    private List<String> codes = new ArrayList<>();
    private List<String> mvalues = new ArrayList<>();
    private List<String> ovalues = new ArrayList<>();
    private List<String> declareColumns = new ArrayList<>();
    private List<String> dbmsLobCreates = new ArrayList<>();
    private List<String> dbmsLobApends = new ArrayList<>();
    private String idCode;
    private String idValue;

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public List<String> getMvalues() {
        return mvalues;
    }

    public void setMvalues(List<String> mvalues) {
        this.mvalues = mvalues;
    }

    public List<String> getOvalues() {
        return ovalues;
    }

    public void setOvalues(List<String> ovalues) {
        this.ovalues = ovalues;
    }

    public List<String> getDeclareColumns() {
        return declareColumns;
    }

    public void setDeclareColumns(List<String> declareColumns) {
        this.declareColumns = declareColumns;
    }

    public List<String> getDbmsLobCreates() {
        return dbmsLobCreates;
    }

    public void setDbmsLobCreates(List<String> dbmsLobCreates) {
        this.dbmsLobCreates = dbmsLobCreates;
    }

    public List<String> getDbmsLobApends() {
        return dbmsLobApends;
    }

    public void setDbmsLobApends(List<String> dbmsLobApends) {
        this.dbmsLobApends = dbmsLobApends;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }
}
