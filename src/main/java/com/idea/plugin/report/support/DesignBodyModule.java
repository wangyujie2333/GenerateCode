package com.idea.plugin.report.support;

import com.idea.plugin.report.support.module.DesignInfo;
import com.idea.plugin.report.support.module.DesignNeedInfo;

public class DesignBodyModule extends DesignModule {
    DesignNeedInfo designNeedInfo;

    public DesignBodyModule(DesignInfo designInfo) {
        super(designInfo);
    }

    public void setDesignNeedInfo(DesignNeedInfo designNeedInfo) {
        this.designNeedInfo = designNeedInfo;
    }

    public String need() {
        return designNeedInfo.getNeed();
    }

    public String content() {
        return designNeedInfo.getContent();
    }

    public String page() {
        return designNeedInfo.getPage();
    }

    public String design() {
        return designNeedInfo.getDesign();
    }

    public String testscope() {
        return designNeedInfo.getTestscope();
    }

    public String designlogic() {
        return designNeedInfo.getDesignlogic();
    }

    public String classstruct() {
        return designNeedInfo.getClassstruct();
    }

    public String datatruct() {
        return designNeedInfo.getDatatruct();
    }

    public String frontend() {
        return designNeedInfo.getFrontend();
    }

    public String backend() {
        return designNeedInfo.getBackend();
    }

}
