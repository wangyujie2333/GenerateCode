package com.idea.plugin.translator.impl;

import com.idea.plugin.utils.HttpUtil;
import com.idea.plugin.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.intellij.images.editor.actions.BackgroundImageDialog;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class YoudaoTranslator extends AbstractTranslator {


    @Override
    public String doTranslate(String text) {
        try {
            YoudaoResponse response = JsonUtil.fromJson(HttpUtil.get(getYoudaoUrl(text)), YoudaoResponse.class);
            return Objects.requireNonNull(response).getTranslateResult().stream()
                    .map(translateResults -> translateResults.stream().map(TranslateResult::getTgt).collect(Collectors.joining(" ")))
                    .findAny().orElse(StringUtils.EMPTY);
        } catch (Exception e) {
            while (retries < 10) {
                ++retries;
                return doTranslate(text);
            }
            return StringUtils.EMPTY;
        }
    }

    public static class YoudaoResponse {
        //{"type":"EN2ZH_CN","errorCode":0,"elapsedTime":0,"translateResult":[[{"src":"writeDoc file","tgt":"写文件"}]]}
        private String type;
        private int errorCode;
        private int elapsedTime;
        private List<List<TranslateResult>> translateResult;

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setElapsedTime(int elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        public int getElapsedTime() {
            return elapsedTime;
        }

        public void setTranslateResult(List<List<TranslateResult>> translateResult) {
            this.translateResult = translateResult;
        }

        public List<List<TranslateResult>> getTranslateResult() {
            return translateResult;
        }

    }

    public static class TranslateResult {

        private String src;
        private String tgt;

        public void setSrc(String src) {
            this.src = src;
        }

        public String getSrc() {
            return src;
        }

        public void setTgt(String tgt) {
            this.tgt = tgt;
        }

        public String getTgt() {
            return tgt;
        }

    }
}
