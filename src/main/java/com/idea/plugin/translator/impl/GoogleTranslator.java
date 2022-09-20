package com.idea.plugin.translator.impl;

import com.idea.plugin.utils.HttpUtil;
import com.idea.plugin.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class GoogleTranslator extends AbstractTranslator {


    @Override
    public String doTranslate(String text) {
        try {
            GoogleResponse response = JsonUtil.fromJson(HttpUtil.get(getGoogleUrl(text)), GoogleResponse.class);
            return Objects.requireNonNull(response).getDict().stream()
                    .map(translateResults -> translateResults.getEntry().stream().map(GoogleResponse.DictBean.EntryBean::getWord).collect(Collectors.joining(" ")))
                    .findAny().orElse(StringUtils.EMPTY);
        } catch (Exception e) {
            while (retries < 10) {
                ++retries;
                return doTranslate(text);
            }
            return StringUtils.EMPTY;
        }
    }

    public static class GoogleResponse {


        /**
         * sentences : [{"trans":"结果","orig":"result","backend":2},{"translit":"Jiéguǒ","src_translit":"riˈzəlt"}]
         * dict : [{"pos":"名词","terms":["结果","成果","效果","成绩","产物","成效","收获","功","名堂"],"entry":[{"word":"结果","reverse_translation":["result","outcome","consequence","effect","consequent","upshot"],"score":0.67663383},{"word":"成果","reverse_translation":["achievement","result","gain","profit","consequent","sequel"],"score":0.02749503},{"word":"效果","reverse_translation":["effect","result","sound effects","consequent","sequel"],"score":0.011642128},{"word":"成绩","reverse_translation":["score","achievement","result","mark"],"score":0.0039610346},{"word":"产物","reverse_translation":["product","result","outcome"],"score":0.0010999396},{"word":"成效","reverse_translation":["effect","result"],"score":6.074443E-4},{"word":"收获","reverse_translation":["gain","result","acquisition"],"score":5.921267E-5},{"word":"功","reverse_translation":["merit","achievement","meritorious service","accomplishment","exploit","result"],"score":5.7390887E-5},{"word":"名堂","reverse_translation":["variety","result","item"],"score":2.123383E-6}],"base_form":"result","pos_enum":1},{"pos":"动词","terms":["导致","致使","酿"],"entry":[{"word":"导致","reverse_translation":["lead to","cause","result","bring about","create"],"score":0.45783335},{"word":"致使","reverse_translation":["cause","result","occasion"],"score":8.174057E-4},{"word":"酿","reverse_translation":["brew","ferment","lead to","brew up","result","make wine"],"score":4.6644533E-7}],"base_form":"result","pos_enum":2}]
         * src : en
         * ld_result : {"srclangs":["en"],"srclangs_confidences":[1],"extended_srclangs":["en"]}
         */

        private String src;
        private List<SentencesBean> sentences;
        private List<DictBean> dict;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public List<SentencesBean> getSentences() {
            return sentences;
        }

        public void setSentences(List<SentencesBean> sentences) {
            this.sentences = sentences;
        }

        public List<DictBean> getDict() {
            return dict;
        }

        public void setDict(List<DictBean> dict) {
            this.dict = dict;
        }

        public static class SentencesBean {
            /**
             * trans : 结果
             * orig : result
             * backend : 2
             * translit : Jiéguǒ
             * src_translit : riˈzəlt
             */

            private String trans;
            private String orig;
            private int backend;
            private String translit;
            private String srcTranslit;

            public String getTrans() {
                return trans;
            }

            public void setTrans(String trans) {
                this.trans = trans;
            }

            public String getOrig() {
                return orig;
            }

            public void setOrig(String orig) {
                this.orig = orig;
            }

            public int getBackend() {
                return backend;
            }

            public void setBackend(int backend) {
                this.backend = backend;
            }

            public String getTranslit() {
                return translit;
            }

            public void setTranslit(String translit) {
                this.translit = translit;
            }

            public String getSrcTranslit() {
                return srcTranslit;
            }

            public void setSrcTranslit(String srcTranslit) {
                this.srcTranslit = srcTranslit;
            }

        }

        public static class DictBean {
            /**
             * pos : 名词
             * terms : ["结果","成果","效果","成绩","产物","成效","收获","功","名堂"]
             * entry : [{"word":"结果","reverse_translation":["result","outcome","consequence","effect","consequent","upshot"],"score":0.67663383},{"word":"成果","reverse_translation":["achievement","result","gain","profit","consequent","sequel"],"score":0.02749503},{"word":"效果","reverse_translation":["effect","result","sound effects","consequent","sequel"],"score":0.011642128},{"word":"成绩","reverse_translation":["score","achievement","result","mark"],"score":0.0039610346},{"word":"产物","reverse_translation":["product","result","outcome"],"score":0.0010999396},{"word":"成效","reverse_translation":["effect","result"],"score":6.074443E-4},{"word":"收获","reverse_translation":["gain","result","acquisition"],"score":5.921267E-5},{"word":"功","reverse_translation":["merit","achievement","meritorious service","accomplishment","exploit","result"],"score":5.7390887E-5},{"word":"名堂","reverse_translation":["variety","result","item"],"score":2.123383E-6}]
             * base_form : result
             * pos_enum : 1
             */

            private String pos;
            //        private String baseForm;
            private int posEnum;
            //        private List<String> terms;
            private List<DictBean.EntryBean> entry;

            public String getPos() {
                return pos;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public int getPosEnum() {
                return posEnum;
            }

            public void setPosEnum(int posEnum) {
                this.posEnum = posEnum;
            }

            public List<DictBean.EntryBean> getEntry() {
                return entry;
            }

            public void setEntry(List<DictBean.EntryBean> entry) {
                this.entry = entry;
            }

            public static class EntryBean {
                /**
                 * word : 结果
                 * reverse_translation : ["result","outcome","consequence","effect","consequent","upshot"]
                 * score : 0.67663383
                 */
                private String word;
                private double score;
                private List<String> reverseTranslation;

                public String getWord() {
                    return word;
                }

                public void setWord(String word) {
                    this.word = word;
                }

                public double getScore() {
                    return score;
                }

                public void setScore(double score) {
                    this.score = score;
                }

                public List<String> getReverseTranslation() {
                    return reverseTranslation;
                }

                public void setReverseTranslation(List<String> reverseTranslation) {
                    this.reverseTranslation = reverseTranslation;
                }
            }
        }
    }
}
