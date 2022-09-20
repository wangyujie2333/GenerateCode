package com.idea.plugin.translator;

import com.idea.plugin.utils.HttpUtil;
import com.idea.plugin.utils.ThreadLocalUtils;
import com.idea.plugin.utils.TkUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

public class TranslatorConfig {
    public static String GOOGLE_TRANSLATOR = "谷歌翻译";
    public static String YOUDAO_TRANSLATOR = "有道翻译";
    public static String BAIDU_TRANSLATOR = "百度翻译";

    public static final Pattern PATTERN = compile("[\u4e00-\u9fa5]");
    public static final String ZH_CN_TO_EN = "zhCnToEn";
    public static final String EN_TO_ZH_CN = "enToZhCn";

    public static final String GOOGLE_CH2EN_URL = "https://translate.google.cn/translate_a/single?client=gtx&dt=t&dt=bd&dt=rm&dj=1&ie=UTF-8&oe=UTF-8&sl=en&tl=zh-CN&hl=zh-CN&tk=%s&q=%s";
    public static final String GOOGLE_EN2CH_URL = "https://translate.google.cn/translate_a/single?client=gtx&dt=t&dt=bd&dt=rm&dj=1&ie=UTF-8&oe=UTF-8&sl=zh-CN&tl=en&hl=zh-CN&tk=%s&q=%s";
    public static final String BAIDU_TRANSLATE_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate?from=auto&to=auto&appid=%s&salt=%s&sign=%s&q=%s";
    public static final String YOUDAO_CH2EN_URL = "http://fanyi.youdao.com/translate?&doctype=json&type=ZH_CN2EN&i=%s";
    public static final String YOUDAO_EN2CH_URL = "http://fanyi.youdao.com/translate?&doctype=json&type=EN2ZH_CN&i=%s";

    public static String getGoogleUrl(String word) {
        if (ZH_CN_TO_EN.equals(getLantype(word))) {
            return String.format(GOOGLE_CH2EN_URL, TkUtil.tk(word), HttpUtil.encode(word));
        } else {
            return String.format(GOOGLE_EN2CH_URL, TkUtil.tk(word), HttpUtil.encode(word));
        }
    }

    public static String getBaiduUrl(String appId, String token, String word) {
        String salt = RandomStringUtils.randomNumeric(16);
        String sign = DigestUtils.md5Hex(appId + word + salt + token);
        return String.format(BAIDU_TRANSLATE_URL, appId, salt, sign, HttpUtil.encode(word));
    }

    public static String getYoudaoUrl(String word) {
        if (ZH_CN_TO_EN.equals(getLantype(word))) {
            return String.format(YOUDAO_CH2EN_URL, HttpUtil.encode(word));
        } else {
            return String.format(YOUDAO_EN2CH_URL, HttpUtil.encode(word));
        }
    }

    public static String getLantype(String word) {
        String lan_type = ThreadLocalUtils.get(String.class, "LAN_TYPE" + word);
        if (StringUtils.isEmpty(lan_type)) {
            Matcher m = PATTERN.matcher(word.trim());
            lan_type = m.find() ? ZH_CN_TO_EN : EN_TO_ZH_CN;
            ThreadLocalUtils.set(String.class, lan_type, "LAN_TYPE" + word);
        }
        return lan_type;
    }

    public static String split(String word) {
        if (EN_TO_ZH_CN.equals(getLantype(word))) {
            word = word.replaceAll("(?<=[^A-Z])[A-Z][^A-Z]", "_$0");
            word = word.replaceAll("[A-Z]{2,}", "_$0");
            word = word.replaceAll("(^[A-Z]){1}[_]{1}", "");
            word = word.replaceAll("_+", "_");
            return Arrays.stream(word.split("_")).map(String::toLowerCase).collect(Collectors.joining(" "));
        }
        return word;
    }


}
