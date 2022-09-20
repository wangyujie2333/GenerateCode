package com.idea.plugin.setting.support;

import com.idea.plugin.translator.impl.AbstractTranslator;
import com.idea.plugin.translator.impl.BaiduTranslator;
import com.idea.plugin.translator.impl.GoogleTranslator;
import com.idea.plugin.translator.impl.YoudaoTranslator;
import org.apache.commons.lang3.StringUtils;

public class SettingConfigVO {
    public String author;
    public Boolean replace = false;
    public String replaceText = "，,|。.|；;|！!|？?|：:|“\"|”\"|‘'|、/|（(|）)|【[|】]|《<|》>|";
    public String translate = "有道翻译";
    public String appId = "20180530000169357";
    public String token = "VSm4LcHQoVxpfYAcL4nK";

    public AbstractTranslator youdaoTranslator = new YoudaoTranslator();
    public AbstractTranslator googleTranslator = new GoogleTranslator();
    public AbstractTranslator baiduTranslator = new BaiduTranslator();

    public String getAuthor() {
        if (StringUtils.isEmpty(author)) {
            author = System.getProperty("user.name");
        }
        return author;
    }

    public void setAuthor(String author) {
        if (StringUtils.isEmpty(author)) {
            this.author = System.getProperty("user.name");
        } else {
            this.author = author;
        }
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

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AbstractTranslator getYoudaoTranslator() {
        return youdaoTranslator;
    }

    public void setYoudaoTranslator(AbstractTranslator youdaoTranslator) {
        this.youdaoTranslator = youdaoTranslator;
    }

    public AbstractTranslator getGoogleTranslator() {
        return googleTranslator;
    }

    public void setGoogleTranslator(AbstractTranslator googleTranslator) {
        this.googleTranslator = googleTranslator;
    }

    public AbstractTranslator getBaiduTranslator() {
        return baiduTranslator;
    }

    public void setBaiduTranslator(AbstractTranslator baiduTranslator) {
        this.baiduTranslator = baiduTranslator;
    }
}
