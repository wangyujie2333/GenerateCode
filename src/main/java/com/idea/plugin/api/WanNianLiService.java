package com.idea.plugin.api;

import com.idea.plugin.utils.HttpUtil;

public class WanNianLiService implements APIConstand<WanNianLiVO> {

    public WanNianLiVO getApiResult() {
        return HttpUtil.get(APIConstand.API_WAN_NIAN_LI, WanNianLiVO.class);
    }
}
