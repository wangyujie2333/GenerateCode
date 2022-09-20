package com.idea.plugin.document.support;

public class JavaSetGetVO {

    private Boolean isNoneConstructor = true;
    private Boolean isAllConstructor;
    private Boolean isSet = true;
    private Boolean isGet = true;
    private Boolean isSGParent;
    private Boolean isSGInner;

    private static JavaSetGetVO instance;

    public JavaSetGetVO() {
    }

    public static JavaSetGetVO getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new JavaSetGetVO();
        return instance;
    }


    public void setIsNoneConstructor(Boolean isNoneConstructor) {
        this.isNoneConstructor = isNoneConstructor;
    }

    public Boolean getIsNoneConstructor() {
        return isNoneConstructor != null && isNoneConstructor;
    }

    public void setIsAllConstructor(Boolean isAllConstructor) {
        this.isAllConstructor = isAllConstructor;
    }

    public Boolean getIsAllConstructor() {
        return isAllConstructor != null && isAllConstructor;
    }

    public void setIsSet(Boolean isSet) {
        this.isSet = isSet;
    }

    public Boolean getIsSet() {
        return isSet != null && isSet;
    }

    public void setIsGet(Boolean isGet) {
        this.isGet = isGet;
    }

    public Boolean getIsGet() {
        return isGet != null && isGet;
    }

    public void setIsSGParent(Boolean isSGParent) {
        this.isSGParent = isSGParent;
    }

    public Boolean getIsSGParent() {
        return isSGParent != null && isSGParent;
    }

    public void setIsSGInner(Boolean isSGInner) {
        this.isSGInner = isSGInner;
    }

    public Boolean getIsSGInner() {
        return isSGInner != null && isSGInner;
    }

}
