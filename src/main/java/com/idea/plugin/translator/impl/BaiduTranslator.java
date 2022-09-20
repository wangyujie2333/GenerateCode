package com.idea.plugin.translator.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.SettingConfigVO;
import com.idea.plugin.utils.HttpUtil;
import com.idea.plugin.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;


public class BaiduTranslator extends AbstractTranslator {

    @Override
    public String doTranslate(String text) {
        try {
            SettingConfigVO config = ToolSettings.getSettingConfig();
            BaiduResponse response = JsonUtil.fromJson(HttpUtil.get(getBaiduUrl(config.appId, config.token, text)), BaiduResponse.class);
            return Objects.requireNonNull(response).getTransResult().stream().map(TransResult::getDst)
                    .findAny().orElse(StringUtils.EMPTY);
        } catch (Exception e) {
            while (retries < 10) {
                ++retries;
                return doTranslate(text);
            }
            return StringUtils.EMPTY;
        }
    }

    public static class BaiduResponse {
        @JsonProperty("error_code")
        private String errorCode;
        @JsonProperty("error_msg")
        private String errorMsg;
        private String from;
        private String to;
        @JsonProperty("trans_result")
        private List<TransResult> transResult;

        public void setFrom(String from) {
            this.from = from;
        }

        public String getFrom() {
            return from;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getTo() {
            return to;
        }

        public void setTransResult(List<TransResult> transResult) {
            this.transResult = transResult;
        }

        public List<TransResult> getTransResult() {
            return transResult;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    public static class TransResult {

        private String src;
        private String dst;

        public void setSrc(String src) {
            this.src = src;
        }

        public String getSrc() {
            return src;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }

        public String getDst() {
            return dst;
        }

    }
}
