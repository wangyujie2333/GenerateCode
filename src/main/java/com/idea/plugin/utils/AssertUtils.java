package com.idea.plugin.utils;

public class AssertUtils {

    public static void assertIsTrue(Boolean flag, String message) {
        if (!flag) {
            throw new RuntimeException(message);
        }
    }

}
