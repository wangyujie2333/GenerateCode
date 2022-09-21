package com.idea.plugin.utils;

import com.idea.plugin.setting.template.TemplateTaskPathEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class StringUtil {

    private final static char UNDERSCORE = '_';

    private static final Pattern pattern = Pattern.compile("(\\$\\{)([\\w -.]+)(})");

    private static final String ALL = "all";
    private static final String COUNT = "count";
    private static final String SUM = "sum";
    private static final String LIKE = "like";
    private static final String NOTLIKE = "notlike";
    private static final String EQ = "eq";


    /**
     * 字符串转换成常量
     *
     * @param str
     * @return
     */
    public static String textToConstant(String str) {
        return textToWords(str).replaceAll("(\\s+)([a-zA-Z])", "_$2").toUpperCase();
    }

    /**
     * 单词转下划线命名
     *
     * @param str
     * @return
     */
    public static String textToUnderscoreCase(String str, Boolean isUpper) {
        str = textToWords(str).replaceAll("(\\s+)([a-zA-Z])", "_$2");
        if (Boolean.TRUE.equals(isUpper)) {
            str = ((char) (str.charAt(0) - 32)) + str.substring(1);
        }
        return str;
    }

    /**
     * 单词转短横线命名
     *
     * @param str
     * @return
     */
    public static String textToKebabCase(String str, Boolean isUpper) {
        str = textToWords(str).replaceAll("(\\s+)([a-zA-Z])", "-$2");
        if (Boolean.TRUE.equals(isUpper)) {
            str = ((char) (str.charAt(0) - 32)) + str.substring(1);
        }
        return str;
    }

    /**
     * 单词转大小驼峰命名
     *
     * @param str
     * @param isUpper true 大驼峰 false 小驼峰
     * @return
     */
    public static String textToCamelCase(String str, Boolean isUpper) {
        str = textToWords(str);
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (i == 0 && Boolean.TRUE.equals(isUpper)) {
                stringBuilder.append(Character.toUpperCase(chars[i]));
                continue;
            }
            if (chars[i] == ' ' && chars[i + 1] >= 'A' && chars[i + 1] <= 'z') {
                stringBuilder.append(Character.toUpperCase(chars[++i]));
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 拆分单词
     *
     * @param str
     * @return
     */
    public static String textToWords(String str) {
        return textToWords(str, false);
    }

    public static String textToWords(String str, boolean digitBlank) {
        //selectText  select Text select_text select_Text SELECT_TEXT
        str = str.replaceAll("([-|_|\\s|\n|\r|\\.]+)([a-zA-Z])", " $2");
        if (isAllUpperCase(str, ' ')) {
            return str.toLowerCase();
        }
        char c;
        char previousChar = ' ';
        StringBuilder stringBuilder = new StringBuilder();
        boolean previousDigit = false;
        for (int i = 0, len = str.length(); i < len; i++) {
            c = str.charAt(i);
            boolean digit = Character.isDigit(c);
            if (i != 0 && i + 1 < str.length() && Character.isUpperCase(c) && Character.isLowerCase(str.charAt(i + 1))) {
                stringBuilder.append(" ").append(c);
            } else if (Character.isLowerCase(previousChar) && Character.isUpperCase(c)) {
                stringBuilder.append(" ").append(c);
            } else if (digitBlank && !previousDigit && digit) {
                stringBuilder.append(" ").append(c);
            } else if (digitBlank && previousChar == '.') {
                stringBuilder.append(" ").append(c);
            } else {
                stringBuilder.append(c);
            }
            previousChar = c;
            previousDigit = digit;

        }
        return stringBuilder.toString().toLowerCase();
    }

    /**
     * 除了指定字符全是大写
     *
     * @param str
     * @return
     */
    private static boolean isAllUpperCase(String str, Character... characters) {
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (!Character.isUpperCase(c)) {
                boolean flag = false;
                for (Character character : characters) {
                    if (c == character) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取所有格式的字符串
     *
     * @param text
     * @return
     */
    public static LinkedHashSet<String> getAllCase(String text) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add(StringUtil.textToCamelCase(text, false));
        set.add(StringUtil.textToCamelCase(text, true));
        set.add(StringUtil.textToConstant(text));
        set.add(StringUtil.textToUnderscoreCase(text, false));
        set.add(StringUtil.textToKebabCase(text, false));
        set.add(StringUtil.textToKebabCase(text, true));
        set.add(StringUtil.textToWords(text));
        set.add(text.toUpperCase());
        set.add(text.toLowerCase());
        return set;
    }

    /**
     * 获取所有格式的字符串(用于翻译结果)
     *
     * @param text
     * @return
     */
    public static List<String> getAllTranslateCase(String text) {
        List<String> list = new ArrayList<>();
        list.add(StringUtil.textToConstant(text));
        list.add(StringUtil.textToCamelCase(text, false));
        list.add(StringUtil.textToCamelCase(text, true));
        list.add(StringUtil.textToKebabCase(text, false));
        return list;
    }

    public static String getResultByTemplate(String templateStr, Map<String, Object> infoJsonMap) {
        Matcher matcher = pattern.matcher(templateStr);
        Object[] args = new Object[100];
        int i = 0;
        while (matcher.find()) {
            args[i] = "";
            try {
                String key = matcher.group(2);
                if (key.startsWith("now")) {
                    args[i] = now(key.substring(3).trim());
                } else if (key.startsWith("week")) {
                    args[i] = week();
                } else if (key.startsWith("quarter")) {
                    args[i] = quarter();
                } else if (key.startsWith("year")) {
                    args[i] = year();
                } else {
                    if (infoJsonMap.containsKey(key)) {
                        args[i] = infoJsonMap.get(key) == null ? "" : infoJsonMap.get(key);
                    } else {
                        if (key.contains(".")) {
                            List<Map<String, Object>> bodyInfoMapList = new ArrayList<>();
                            String[] params = key.split("\\.");
                            if (!ALL.equalsIgnoreCase(params[1])) {
                                bodyInfoMapList = new ArrayList<>();
                                bodyInfoMapList.add(infoJsonMap);
                            } else {
                                Object bodyObj = infoJsonMap.get(TemplateTaskPathEnum.BODY.getCode());
                                if (bodyObj instanceof Map) {
                                    bodyInfoMapList.add((Map<String, Object>) bodyObj);
                                } else if (bodyObj instanceof List) {
                                    for (Object mapObj : ((List<?>) bodyObj)) {
                                        if (mapObj instanceof Map) {
                                            bodyInfoMapList.add((Map<String, Object>) mapObj);
                                        }
                                    }
                                }
                            }
                            if (CollectionUtils.isEmpty(bodyInfoMapList)) {
                                continue;
                            }
                            String subkey = params[0];
                            String resultKey = params[0];
                            for (int j = 1; j < params.length; j++) {
                                String param = params[j].toLowerCase();
                                String preResultKey = resultKey;
                                resultKey = resultKey + "." + param;
                                if (ALL.equalsIgnoreCase(params[j])) {
                                    continue;
                                }
                                List<String> valueList = null;
                                if (infoJsonMap.get(resultKey) instanceof List) {
                                    valueList = (List<String>) infoJsonMap.get(resultKey);
                                } else {
                                    Map<String, List<Map.Entry<String, Object>>> bodyInfoKeyListMap = bodyInfoMapList.stream().flatMap(bodyInfoMap -> bodyInfoMap.entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey));
                                    if (bodyInfoKeyListMap.containsKey(subkey)) {
                                        valueList = bodyInfoKeyListMap.get(subkey).stream().map(bodyInfoValueMap -> bodyInfoValueMap.getValue().toString()).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                                    }
                                }
                                if (CollectionUtils.isEmpty(valueList)) {
                                    continue;
                                }
                                if (param.startsWith(LIKE)) {
                                    String condition = param.replaceAll(LIKE, "");
                                    infoJsonMap.put(resultKey, valueList.stream().filter(value -> value.contains(condition)).collect(Collectors.joining("\n")));
                                }
                                if (param.startsWith(NOTLIKE)) {
                                    String condition = param.replaceAll(NOTLIKE, "");
                                    infoJsonMap.put(resultKey, valueList.stream().filter(value -> value.contains(condition)).collect(Collectors.joining("\n")));
                                }
                                if (param.equals(COUNT)) {
                                    infoJsonMap.put(resultKey, valueList.stream().filter(StringUtils::isNotBlank).count());
                                }
                                if (param.equals(SUM)) {
                                    infoJsonMap.put(resultKey, String.join("  \n", valueList));
                                }
                            }
                            args[i] = infoJsonMap.get(resultKey);
                        } else {
                            args[i] = "";
                        }
                    }
                }
                ++i;
            } catch (Exception e) {
                args[i] = "";
                ++i;
            }
        }
        templateStr = templateStr.replaceAll("%", "===!===");
        templateStr = templateStr.replaceAll("\\$\\{[\\w -.]+}", "%s");
        String format = String.format(templateStr, args);
        format = format.replaceAll("===!===", "%");
        return format;
    }

    public static String now() {
        return now(DateUtils.YYYY_MM_DD_HH_MM_SS);
    }

    public static String now(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = DateUtils.YYYY_MM_DD_HH_MM_SS;
        }
        return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), pattern);
    }

    public static String week() {
        LocalDateTime now = LocalDateTime.now();
        Calendar calendar = DateUtils.DateToCalendar(now);
        return String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
    }

    public static String year() {
        LocalDateTime now = LocalDateTime.now();
        return String.valueOf(now.getYear());
    }

    public static String quarter() {
        LocalDateTime now = LocalDateTime.now();
        Calendar calendar = DateUtils.DateToCalendar(now);
        int number = calendar.get(Calendar.MONTH);
        return now.getYear() + "-" + (number % 3 == 0 ? number / 3 : number / 3 + 1);
    }

    public static String getBlank(String code, Integer max) {
        StringBuilder sb = new StringBuilder();
        for (int i = code.length(); i < max; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
