package com.idea.plugin.setting;

import com.idea.plugin.setting.support.*;

public class ToolsConfigVO {

    public CopyBuildConfigVO copyBuildConfigVO;
    public ReportConfigVO reportConfigVO;
    public SettingConfigVO settingConfigVO;
    public TableConfigVO tableConfigVO;
    public TranslateConfigVO translateConfigVO;
    public JavaFileConfigVO javaFileConfigVO;

    public CopyBuildConfigVO getCopyBuildConfigVO() {
        return copyBuildConfigVO;
    }

    public void setCopyBuildConfigVO(CopyBuildConfigVO copyBuildConfigVO) {
        this.copyBuildConfigVO = copyBuildConfigVO;
    }

    public ReportConfigVO getReportConfigVO() {
        return reportConfigVO;
    }

    public void setReportConfigVO(ReportConfigVO reportConfigVO) {
        this.reportConfigVO = reportConfigVO;
    }

    public SettingConfigVO getSettingConfigVO() {
        return settingConfigVO;
    }

    public void setSettingConfigVO(SettingConfigVO settingConfigVO) {
        this.settingConfigVO = settingConfigVO;
    }

    public TableConfigVO getTableConfigVO() {
        return tableConfigVO;
    }

    public void setTableConfigVO(TableConfigVO tableConfigVO) {
        this.tableConfigVO = tableConfigVO;
    }

    public TranslateConfigVO getTranslateConfigVO() {
        return translateConfigVO;
    }

    public void setTranslateConfigVO(TranslateConfigVO translateConfigVO) {
        this.translateConfigVO = translateConfigVO;
    }

    public JavaFileConfigVO getJavaFileConfigVO() {
        return javaFileConfigVO;
    }

    public void setJavaFileConfigVO(JavaFileConfigVO javaFileConfigVO) {
        this.javaFileConfigVO = javaFileConfigVO;
    }
}
