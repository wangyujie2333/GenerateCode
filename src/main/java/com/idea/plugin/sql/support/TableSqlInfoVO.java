package com.idea.plugin.sql.support;


import java.util.List;

public class TableSqlInfoVO extends TableInfoVO {

    protected boolean isMergeAddColumnStart = false;
    protected boolean isMergeAddIndexStart = false;
    protected boolean isMergeModifyColumnStart = false;
    private boolean isMergeAddColumnEnd = false;
    private boolean isMergeAddIndexEnd = false;
    private boolean isMergeModifyColumnEnd = false;
    private List<InsertDataInfoVO> insertDataInfoVOS;

    public TableSqlInfoVO() {
    }

    public Boolean getIsMergeAddColumnStart() {
        return isMergeAddColumnStart;
    }

    public void setIsMergeAddColumnStart(Boolean isMergeAddColumnStart) {
        this.isMergeAddColumnStart = isMergeAddColumnStart;
    }

    public Boolean getIsMergeAddIndexStart() {
        return isMergeAddIndexStart;
    }

    public void setIsMergeAddIndexStart(Boolean isMergeAddIndexStart) {
        this.isMergeAddIndexStart = isMergeAddIndexStart;
    }

    public Boolean getIsMergeModifyColumnStart() {
        return isMergeModifyColumnStart;
    }

    public void setIsMergeModifyColumnStart(Boolean isMergeModifyColumnStart) {
        this.isMergeModifyColumnStart = isMergeModifyColumnStart;
    }

    public Boolean getIsMergeAddColumnEnd() {
        return isMergeAddColumnEnd;
    }

    public void setIsMergeAddColumnEnd(Boolean isMergeAddColumnEnd) {
        this.isMergeAddColumnEnd = isMergeAddColumnEnd;
    }

    public Boolean getIsMergeAddIndexEnd() {
        return isMergeAddIndexEnd;
    }

    public void setIsMergeAddIndexEnd(Boolean isMergeAddIndexEnd) {
        this.isMergeAddIndexEnd = isMergeAddIndexEnd;
    }

    public Boolean getIsMergeModifyColumnEnd() {
        return isMergeModifyColumnEnd;
    }

    public void setIsMergeModifyColumnEnd(Boolean isMergeModifyColumnEnd) {
        this.isMergeModifyColumnEnd = isMergeModifyColumnEnd;
    }

    public List<InsertDataInfoVO> getInsertDataInfoVOS() {
        return insertDataInfoVOS;
    }

    public void setInsertDataInfoVOS(List<InsertDataInfoVO> insertDataInfoVOS) {
        this.insertDataInfoVOS = insertDataInfoVOS;
    }
}
