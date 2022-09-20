package com.idea.plugin.api;

public interface APIConstand<T> {
    String API_WAN_NIAN_LI = "https://www.mxnzp.com/api/holiday/single/%s?ignoreHoliday=false&app_id=rgihdrm0kslojqvm&app_secret=WnhrK251TWlUUThqaVFWbG5OeGQwdz09";

    T getApiResult();
}
