package com.idea.plugin.document.support;

public class JavaDocVO {

    private Boolean isCCovered;
    private Boolean isCGenerate = true;
    private Boolean isCParent;
    private Boolean isMCovered;
    private Boolean isMGenerate = true;
    private Boolean isMParent;
    private Boolean isFCovered;
    private Boolean isFGenerate = true;
    private Boolean isFParent;
    private Boolean isCInner;
    private Boolean isMInner;
    private Boolean isFInner;
    private static JavaDocVO instance;

    public static JavaDocVO getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new JavaDocVO();
        return instance;
    }

    public Boolean getCCovered() {
        return isCCovered != null && isCCovered;
    }

    public void setCCovered(Boolean CCovered) {
        isCCovered = CCovered;
    }

    public Boolean getCGenerate() {
        return isCGenerate != null && isCGenerate;
    }

    public void setCGenerate(Boolean CGenerate) {
        isCGenerate = CGenerate;
    }

    public Boolean getCParent() {
        return isCParent != null && isCParent;
    }

    public void setCParent(Boolean CParent) {
        isCParent = CParent;
    }

    public Boolean getMCovered() {
        return isMCovered != null && isMCovered;
    }

    public void setMCovered(Boolean MCovered) {
        isMCovered = MCovered;
    }

    public Boolean getMGenerate() {
        return isMGenerate != null && isMGenerate;
    }

    public void setMGenerate(Boolean MGenerate) {
        isMGenerate = MGenerate;
    }

    public Boolean getMParent() {
        return isMParent != null && isMParent;
    }

    public void setMParent(Boolean MParent) {
        isMParent = MParent;
    }

    public Boolean getFCovered() {
        return isFCovered != null && isFCovered;
    }

    public void setFCovered(Boolean FCovered) {
        isFCovered = FCovered;
    }

    public Boolean getFGenerate() {
        return isFGenerate != null && isFGenerate;
    }

    public void setFGenerate(Boolean FGenerate) {
        isFGenerate = FGenerate;
    }

    public Boolean getFParent() {
        return isFParent != null && isFParent;
    }

    public void setFParent(Boolean FParent) {
        isFParent = FParent;
    }

    public Boolean getCInner() {
        return isCInner != null && isCInner;
    }

    public void setCInner(Boolean CInner) {
        isCInner = CInner;
    }

    public Boolean getMInner() {
        return isMInner != null && isMInner;
    }

    public void setMInner(Boolean MInner) {
        isMInner = MInner;
    }

    public Boolean getFInner() {
        return isFInner != null && isFInner;
    }

    public void setFInner(Boolean FInner) {
        isFInner = FInner;
    }
}
