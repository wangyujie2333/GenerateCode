package com.idea.plugin.setting.template;


import java.util.List;

public class ArchiveVO {
    private Boolean isarchive;
    private String archivePath;
    private List<String> archiveFilePath;
    public List<String> conversionPath;

    public Boolean getIsarchive() {
        return isarchive;
    }

    public void setIsarchive(Boolean isarchive) {
        this.isarchive = isarchive;
    }

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    public List<String> getArchiveFilePath() {
        return archiveFilePath;
    }

    public void setArchiveFilePath(List<String> archiveFilePath) {
        this.archiveFilePath = archiveFilePath;
    }

    public List<String> getConversionPath() {
        return conversionPath;
    }

    public void setConversionPath(List<String> conversionPath) {
        this.conversionPath = conversionPath;
    }
}
