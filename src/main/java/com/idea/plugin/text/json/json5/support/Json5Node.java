package com.idea.plugin.text.json.json5.support;

import java.util.Map;

public class Json5Node {
    private String comment;
    private Map<String, Object> keyValue;

    public Json5Node() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(Map<String, Object> keyValue) {
        this.keyValue = keyValue;
    }
}
