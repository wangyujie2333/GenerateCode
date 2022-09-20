package com.idea.plugin.report.support;

import com.idea.plugin.utils.DateUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateModule {
    private static final Pattern pattern = Pattern.compile("(\\$\\{)(\\w+)(})");


    public String getTemplate(String templateStr) {
        Matcher matcher = pattern.matcher(templateStr);
        Object[] args = new Object[100];
        int i = 0;
        while (matcher.find()) {
            try {
                Method declaredMethod = this.getClass().getDeclaredMethod(matcher.group(2));
                Object invoke = declaredMethod.invoke(this);
                args[i] = invoke == null ? "  " : invoke;
                ++i;
            } catch (Exception e) {
                try {
                    Method declaredMethod = this.getClass().getSuperclass().getDeclaredMethod(matcher.group(2));
                    Object invoke = declaredMethod.invoke(this);
                    args[i] = invoke == null ? "  " : invoke;
                    ++i;
                } catch (Exception ex) {
                    args[i] = "  ";
                }
            }
        }
        templateStr = templateStr.replaceAll("\\$\\{\\w+}", "%s");
        return String.format(templateStr, args);
    }

    public String now() {
        return DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD_HH_MM_SS);
    }
}
