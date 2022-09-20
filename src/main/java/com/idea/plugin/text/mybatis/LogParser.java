package com.idea.plugin.text.mybatis;

import com.idea.plugin.utils.NoticeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LogParser {

    private static final String PREFIX_SQL = "Preparing: ";
    private static final String PREFIX_SQL2 = "SQL: ";
    private static final String PREFIX_PARAMS = "Parameters: ";
    private static final List<String> NON_QUOTED_TYPES = Arrays.asList("Integer", "Long", "Double", "Float", "Boolean");

    public static String toBeautifulSql(String log) {
        String sql = toSql(log);
        if (StringUtils.isEmpty(sql)) {
            NoticeUtil.init("Copy As Executable Sql");
            NoticeUtil.warn("selected log without \"Preparing:\" Or \"SQL:\" line, nothing will send to clipboard!");
        }
        return SqlFormatter.format(sql);
    }


    public static String toSql(String log) {
        if (StringUtils.isEmpty(log)) {
            return "";
        }
        String sqlLine = null;
        String valueLine = null;
        String sqlLine2 = null;
        for (String line : log.split("\n")) {
            if (line.contains(PREFIX_SQL)) {
                sqlLine = line;
            } else if (line.contains(PREFIX_PARAMS)) {
                valueLine = line;
            } else if (line.contains(PREFIX_SQL2)) {
                sqlLine2 = line;
            }
        }
        if (Objects.isNull(sqlLine) && Objects.isNull(sqlLine2)) {
            return "";
        }
        if (StringUtils.isNotEmpty(sqlLine)) {
            int sqlPrefixIndex = sqlLine.indexOf(PREFIX_SQL);
            String originSql = sqlLine.substring(sqlPrefixIndex + PREFIX_SQL.length());
            int paramPrefixIndex = valueLine.indexOf(PREFIX_PARAMS);
            String paramValues = valueLine.substring(paramPrefixIndex + PREFIX_PARAMS.length());

            List<String> originSqlSections = new ArrayList<>(Arrays.asList(originSql.split("\\?")));
            List<String> paramValuesSections = new ArrayList<>(Arrays.asList(paramValues.split(", ")));
            int i = 0;
            StringBuilder sb = new StringBuilder();
            while (originSqlSections.size() > i && paramValuesSections.size() > i) {
                sb.append(originSqlSections.get(i));
                sb.append(parseParam(paramValuesSections.get(i)));
                i++;
            }
            while (originSqlSections.size() > i) {
                sb.append(originSqlSections.get(i));
                i++;
            }
            return sb.toString();
        } else {
            int sqlPrefixIndex = sqlLine2.indexOf(PREFIX_SQL2);
            return sqlLine2.substring(sqlPrefixIndex + PREFIX_SQL2.length());
        }
    }

    /**
     * 解析参数值
     *
     * @param paramValue 参数值字符串
     * @return 参数值
     */
    private static String parseParam(String paramValue) {
        if (StringUtils.isEmpty(paramValue) || paramValue.trim().equals("null")) {
            return paramValue;
        }
        // 括号的索引
        int lastLeftBracketIndex = paramValue.lastIndexOf("(");
        int lastRightBracketIndex = paramValue.lastIndexOf(")");
        // 参数值
        String param = paramValue.substring(0, lastLeftBracketIndex);
        // 参数类型
        String type = paramValue.substring(lastLeftBracketIndex + 1, lastRightBracketIndex);

        return NON_QUOTED_TYPES.contains(type) ? param : String.format("'%s'", param);
    }
}
