package com.idea.plugin.word;

import java.util.Arrays;

public enum WordTypeEnum {
    TRANSLATE(0),
    SNAKE_CASE(1),
    CAMEL_CASE(2),
    UP_CAMEL_CASE(3),
    KEBAB_CASE(4),
    ;
    int postion;

    WordTypeEnum(int postion) {
        this.postion = postion;
    }

    public int getPostion() {
        return postion;
    }

    public static WordTypeEnum indexToEnum(int index) {
        return Arrays.stream(WordTypeEnum.values()).filter(wordTypeEnum -> wordTypeEnum.getPostion() == index).findAny().orElse(null);
    }
}
