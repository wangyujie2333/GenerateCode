package com.idea.plugin.setting.support;


public class CopyBuildConfigVO {
    public Boolean sourceCode;
    public String filePathCache;
    public String folderName;

    public CopyBuildConfigVO() {
    }

    public Boolean getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(Boolean sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getFilePathCache() {
        return filePathCache;
    }

    public void setFilePathCache(String filePathCache) {
        this.filePathCache = filePathCache;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
