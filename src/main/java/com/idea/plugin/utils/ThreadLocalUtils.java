package com.idea.plugin.utils;

import java.util.HashMap;
import java.util.Map;


public class ThreadLocalUtils {
    private static ThreadLocal<Map<Class<?>, Map<Object, Object>>> LOCAL_MAP = new ThreadLocal<>();

    public static void clear() {
        if (LOCAL_MAP.get() != null) {
            LOCAL_MAP.remove();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<?> cls, Object key) {
        if (LOCAL_MAP.get() != null && LOCAL_MAP.get().get(cls) != null) {
            return (T) LOCAL_MAP.get().get(cls).get(key);
        }
        return null;
    }

    public static void remove(Class<?> cls, Object key) {
        if (LOCAL_MAP.get() != null && LOCAL_MAP.get().get(cls) != null) {
            LOCAL_MAP.get().get(cls).remove(key);
        }
    }

    public static void remove(Class<?> cls) {
        if (LOCAL_MAP.get() != null) {
            LOCAL_MAP.get().remove(cls);
        }
    }

    private static void initMap(Class<?> cls) {
        if (LOCAL_MAP.get() == null) {
            Map<Class<?>, Map<Object, Object>> classMap = new HashMap<>(16);
            LOCAL_MAP.set(classMap);
        }
        if (LOCAL_MAP.get().get(cls) == null) {
            Map<Object, Object> map = new HashMap<>(16);
            LOCAL_MAP.get().put(cls, map);
        }
    }

    public static void set(Class<?> cls, Object key, Object obj) {
        initMap(cls);
        LOCAL_MAP.get().get(cls).put(key, obj);
    }

}
