package com.idea.plugin.translator;


public interface Translator {

    /**
     * 英译中或中译英
     *
     * @param text 文本
     * @return {@link String}
     */
    String doTranslate(String text);
}
