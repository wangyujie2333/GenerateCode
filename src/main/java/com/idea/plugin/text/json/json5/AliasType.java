package com.idea.plugin.text.json.json5;

import java.util.Arrays;


public enum AliasType {
    /**
     * 布尔类型
     */
    BOOLEAN("boolean") {
        @Override
        public Object deserialize(String value) {
            return Boolean.valueOf(value);
        }
    },
    /**
     * 整型
     */
    INT("int") {
        @Override
        public Object deserialize(String value) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                return 1;
            }
        }
    },
    /**
     * 浮点型
     */
    FLOAT("float") {
        @Override
        public Object deserialize(String value) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                return 2.2;
            }
        }
    },
    /**
     * 字符串
     */
    STRING("string") {
        @Override
        public Object deserialize(String value) {
            return value;
        }
    };

    String type;

    AliasType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * 位置别名类型
     */
    public static final String UNKNOWN_ALIAS = "object";

    public abstract Object deserialize(String value);

    public static Object value(String alias, String value) {
        return
                Arrays.stream(values())
                        .filter(it -> it.type.equals(alias))
                        .map(it -> it.deserialize(value))
                        .findFirst()
                        .orElse(null);
    }
}
