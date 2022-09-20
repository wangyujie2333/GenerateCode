package com.idea.plugin.setting.support;


import com.idea.plugin.word.WordTypeEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TranslateConfigVO {

    public Map<String, String> cacheMap = new ConcurrentHashMap<>();
    public Map<String, String> wordJsonMap = new ConcurrentHashMap<>();

    public WordTypeEnum wordTypeEnum = WordTypeEnum.TRANSLATE;

    public void setWordTypeEnum(WordTypeEnum wordTypeEnum) {
        if (wordTypeEnum != null) {
            this.wordTypeEnum = wordTypeEnum;
        } else {
            this.wordTypeEnum = WordTypeEnum.TRANSLATE;
        }
    }

    public Map<String, String> getCacheMap() {
        return cacheMap;
    }

    public void setCacheMap(Map<String, String> cacheMap) {
        this.cacheMap = cacheMap;
    }

    public Map<String, String> getWordJsonMap() {
        return wordJsonMap;
    }

    public void setWordJsonMap(Map<String, String> wordJsonMap) {
        this.wordJsonMap = wordJsonMap;
    }

    public WordTypeEnum getWordTypeEnum() {
        return wordTypeEnum;
    }
}
