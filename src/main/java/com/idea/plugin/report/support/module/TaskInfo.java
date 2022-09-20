package com.idea.plugin.report.support.module;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskInfo {
    private final int length = 80;
    private String need;
    private String bug;
    private String other;

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = getStr(this.need, need);
    }

    public String getBug() {
        return bug;
    }

    public void setBug(String bug) {
        this.bug = getStr(this.bug, bug);
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = getStr(this.other, other);
    }


    public List<String> getNeedList() {
        return getStrList(need);
    }

    public List<String> getBugList() {
        return getStrList(bug);
    }

    public List<String> getOtherList() {
        return getStrList(other);
    }

    public String getStr(String str, String s) {
        if (StringUtils.isNotBlank(s)) {
            if (str == null) {
                str = s + "\n";
            } else {
                str = str + s + "\n";
            }
        }
        return str;
    }

    public List<String> getStrList(String str) {
        List<String> strList = new ArrayList<>();
        if (StringUtils.isNotBlank(str)) {
            String[] split = str.split("\n");
            for (String s : split) {
                if (StringUtils.isNotBlank(s.trim())) {
                    if (length < s.length()) {
                        s = s.substring(0, length);
                    }
                    if (!strList.contains(s)) {
                        strList.add(s);
                    }
                }
            }
        }
        return strList;
    }
}
