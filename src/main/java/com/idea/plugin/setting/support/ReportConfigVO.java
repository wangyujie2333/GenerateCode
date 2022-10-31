package com.idea.plugin.setting.support;

import com.idea.plugin.api.WanNianLiVO;
import com.idea.plugin.report.support.enums.ReportTypeEnum;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.template.*;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.JsonUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportConfigVO extends BaseConfigVO {

    public String author;
    public Long period;
    public String filePath;
    public String backupfromPath;
    public String backuptoPath;

    public ArchiveVO archive;
    public DayTemplateVO dayTemplate;
    public SubTemplateVO subTemplate;
    public KeyTemplateVO keyTemplate;
    public MdTemplateVO mdTemplate;
    public SqlTemplateVO sqlTemplateVO;
    public JavaTemplateVO javaTemplateVO;
    public LogTemplateVO logTemplate;
    public Map<String, String> wordTemplate;
    public Map<String, String> wordTranslate;
    private Setting setting;


    public WanNianLiVO wanNianLiVO;


    public ReportConfigVO() {
    }

    public String getMdpath(ReportTypeEnum type) {
        if (dayTemplate == null) {
            return "";
        }
        switch (type) {
            case DAY:
                return dayTemplate.getDayPath();
            case WEEK:
                return dayTemplate.getWeekPath();
        }
        return dayTemplate.getDayPath();
    }

    public void copy(ReportConfigVO config) {
        super.copy(config);
        if (subTemplate != null) {
            keyTemplate = JsonUtil.fromJson(FileUtils.readFileStr(config.filePath + subTemplate.keyTemplate), KeyTemplateVO.class);
            mdTemplate = JsonUtil.fromJson(FileUtils.readFileStr(config.filePath + subTemplate.mdTemplate), MdTemplateVO.class);
            sqlTemplateVO = JsonUtil.fromJson(FileUtils.readFileStr(config.filePath + subTemplate.sqlTemplate), SqlTemplateVO.class);
            javaTemplateVO = JsonUtil.fromJson(FileUtils.readFileStr(config.filePath + subTemplate.javatemplate), JavaTemplateVO.class);
            wordTemplate = JsonUtil.fromJson(FileUtils.readFileStr(config.filePath + subTemplate.wordTemplate), Map.class);
            wordTranslate = JsonUtil.fromJson(FileUtils.readFileStr(config.filePath + subTemplate.wordTranslate), Map.class);
        }
        if (setting != null) {
            TranslateConfigVO translateConfig = ToolSettings.getTranslateConfig();
            if (setting.getReplace() != null) {
                SettingConfigVO settingConfig = ToolSettings.getSettingConfig();
                settingConfig.setReplace(setting.getReplace().getReplace());
                settingConfig.setReplaceText(setting.getReplace().getReplaceText());
            }
            if (StringUtils.isNotEmpty(setting.getWordJsonMap())) {
                String wordJson = Arrays.stream(setting.getWordJsonMap().split(";")).map(String::trim).distinct().collect(Collectors.joining("\n"));
                wordJson = JsonUtil.propertyToJson(wordJson);
                Map<String, String> wordTransMap = JsonUtil.fromJson(wordJson, Map.class);
                translateConfig.setWordJsonMap(wordTransMap);
                if (MapUtils.isNotEmpty(wordTransMap)) {
                    translateConfig.cacheMap.putAll(wordTransMap);
                    wordTransMap.forEach((key, value) -> translateConfig.cacheMap.put(value, key));
                }
            }
        }
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBackupfromPath() {
        return backupfromPath;
    }

    public void setBackupfromPath(String backupfromPath) {
        this.backupfromPath = backupfromPath;
    }

    public String getBackuptoPath() {
        return backuptoPath;
    }

    public void setBackuptoPath(String backuptoPath) {
        this.backuptoPath = backuptoPath;
    }

    public ArchiveVO getArchive() {
        return archive;
    }

    public void setArchive(ArchiveVO archive) {
        this.archive = archive;
    }

    public DayTemplateVO getDayTemplate() {
        return dayTemplate;
    }

    public void setDayTemplate(DayTemplateVO dayTemplate) {
        this.dayTemplate = dayTemplate;
    }

    public SubTemplateVO getSubTemplate() {
        return subTemplate;
    }

    public void setSubTemplate(SubTemplateVO subTemplate) {
        this.subTemplate = subTemplate;
    }

    public KeyTemplateVO getKeyTemplate() {
        return keyTemplate;
    }

    public void setKeyTemplate(KeyTemplateVO keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public MdTemplateVO getMdTemplate() {
        return mdTemplate;
    }

    public void setMdTemplate(MdTemplateVO mdTemplate) {
        this.mdTemplate = mdTemplate;
    }

    public SqlTemplateVO getSqlTemplateVO() {
        return sqlTemplateVO;
    }

    public void setSqlTemplateVO(SqlTemplateVO sqlTemplateVO) {
        this.sqlTemplateVO = sqlTemplateVO;
    }

    public JavaTemplateVO getJavaTemplateVO() {
        return javaTemplateVO;
    }

    public void setJavaTemplateVO(JavaTemplateVO javaTemplateVO) {
        this.javaTemplateVO = javaTemplateVO;
    }

    public Map<String, String> getWordTemplate() {
        return wordTemplate;
    }

    public void setWordTemplate(Map<String, String> wordTemplate) {
        this.wordTemplate = wordTemplate;
    }

    public Map<String, String> getWordTranslate() {
        return wordTranslate;
    }

    public void setWordTranslate(Map<String, String> wordTranslate) {
        this.wordTranslate = wordTranslate;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public WanNianLiVO getWanNianLiVO() {
        if (wanNianLiVO == null) {
            return new WanNianLiVO();
        }
        return wanNianLiVO;
    }

    public void setWanNianLiVO(WanNianLiVO wanNianLiVO) {
        this.wanNianLiVO = wanNianLiVO;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setLogTemplate(LogTemplateVO logTemplate) {
        this.logTemplate = logTemplate;
    }

    public LogTemplateVO getLogTemplate() {
        return logTemplate;
    }


    public static class Setting {
        private Replace replace;
        private String wordJsonMap;


        public Setting() {
        }

        public Replace getReplace() {
            return replace;
        }

        public void setReplace(Replace replace) {
            this.replace = replace;
        }

        public String getWordJsonMap() {
            return wordJsonMap;
        }

        public void setWordJsonMap(String wordJsonMap) {
            this.wordJsonMap = wordJsonMap;
        }
    }

    public static class Replace {
        private Boolean replace;
        private String replaceText;

        public Replace() {
        }

        public Boolean getReplace() {
            return replace;
        }

        public void setReplace(Boolean replace) {
            this.replace = replace;
        }

        public String getReplaceText() {
            return replaceText;
        }

        public void setReplaceText(String replaceText) {
            this.replaceText = replaceText;
        }
    }
}
