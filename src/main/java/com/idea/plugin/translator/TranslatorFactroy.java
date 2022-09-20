package com.idea.plugin.translator;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.SettingConfigVO;
import com.idea.plugin.translator.impl.AbstractTranslator;

public class TranslatorFactroy {


    public static AbstractTranslator create() {
        SettingConfigVO config = ToolSettings.getSettingConfig();
        if (TranslatorConfig.GOOGLE_TRANSLATOR.equals(config.translate)) {
            return config.googleTranslator;
        } else if (TranslatorConfig.YOUDAO_TRANSLATOR.equals(config.translate)) {
            return config.youdaoTranslator;
        } else if (TranslatorConfig.BAIDU_TRANSLATOR.equals(config.translate)) {
            return config.baiduTranslator;
        }
        return config.youdaoTranslator;
    }

    public static String translate(String text) {
        return create().translate(text);
    }
}
