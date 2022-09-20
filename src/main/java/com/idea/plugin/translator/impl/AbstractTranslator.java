package com.idea.plugin.translator.impl;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.TranslateConfigVO;
import com.idea.plugin.translator.Translator;
import com.idea.plugin.translator.TranslatorConfig;
import com.idea.plugin.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class AbstractTranslator extends TranslatorConfig implements Translator {

    protected int retries = 0;

    /**
     * 自动翻译
     *
     * @param text 源
     * @return {@link String}
     */
    public String translate(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        Map<String, String> wordTranslate = ToolSettings.getReportConfig().getWordTranslate();
        String words = StringUtil.textToWords(text, true);
        if (wordTranslate != null) {
            words = Arrays.stream(words.split(" ")).map(s -> {
                s = s.trim();
                if (wordTranslate.containsKey(s)) {
                    s = wordTranslate.get(s);
                }
                return s;
            }).collect(Collectors.joining(" "));
        }
        TranslateConfigVO config = ToolSettings.getTranslateConfig();
        List<String> wrodList = new ArrayList<>();
        for (String word : words.split(" ")) {
            String res = config.cacheMap.get(word);
            if (res != null && res.length() > 0) {
                wrodList.add(res);
                continue;
            }
            retries = 0;
            res = doTranslate(word);
            if (res != null && res.length() > 0) {
                config.cacheMap.put(word, res);
                config.cacheMap.put(res.toLowerCase(), word);
                wrodList.add(res);
            }
        }
        if (ZH_CN_TO_EN.equals(getLantype(words))) {
            String wordStr = "";
            for (String wrod : wrodList) {
                if (!NumberUtils.isDigits(wrod) && !StringUtils.isEmpty(wordStr)) {
                    wrod = " " + wrod;
                }
                wordStr = wordStr + wrod;
            }
            return wordStr;
        } else {
            return String.join("", wrodList);
        }

    }
}
