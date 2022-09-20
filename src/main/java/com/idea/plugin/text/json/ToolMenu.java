package com.idea.plugin.text.json;

import com.google.gson.JsonObject;
import com.idea.plugin.text.json.json5.Json5Reader;
import com.idea.plugin.text.mybatis.LogParser;
import com.idea.plugin.text.mybatis.SqlFormatter;
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.JsonUtil;
import com.idea.plugin.utils.NoticeUtil;
import com.idea.plugin.utils.StringUtil;
import com.intellij.ui.JBColor;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextArea;

import javax.swing.text.BadLocationException;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ToolMenu {

    /**
     * main menu
     */
    JSON("Json", null),
    JSON_FORMAT("Format", ConvertType.JSON) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String json = JsonUtil.prettyJson(input.getText());
                output.setText(json);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    JSON_COMPRESSION("Compression", ConvertType.JSON) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String json = JsonUtil.toJson(input.getText());
                output.setText(json);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    JSON_DOC("FormatDoc", ConvertType.JSON) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String json = Json5Reader.getJson5(input.getText());
                output.setText(json);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    JSON_KEY_DOC("FormatKeyDoc", ConvertType.JSON) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                JsonObject jsonObject = Json5Reader.getFormatKeyDoc(input.getText());
                String json = JsonUtil.prettyJson(jsonObject);
                output.setText(json);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    KEY_DOC("OnlyKeyDoc", ConvertType.YAML) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String keyDoc = Json5Reader.getKeyDoc(input.getText());
                output.setText(keyDoc);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    SQL("Sql", null),
    SQL_MYSQL("Mysql", ConvertType.SQL) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String ddl = Json5Reader.getMysqlDDL(input.getText());
                output.setText(ddl);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    SQL_ORACLE("Oracle", ConvertType.SQL) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String ddl = Json5Reader.getOracleDDL(input.getText());
                output.setText(ddl);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    SQL_LOG("MybatisLog", ConvertType.SQL) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String sql = LogParser.toBeautifulSql(input.getText());
            if (StringUtils.isEmpty(sql)) {
                // 设置错误行背景色
                // 默认设置第一行
                try {
                    input.addLineHighlight(0, JBColor.RED);
                } catch (BadLocationException ex) {
                    NoticeUtil.error(ex);
                }
                // 设置提示信息
                input.setToolTipSupplier((RTextArea rt, MouseEvent me) -> {
                    int offset = 0;
                    try {
                        offset = input.getLineOfOffset(input.viewToModel(me.getPoint()));
                    } catch (BadLocationException ex) {
                        NoticeUtil.error(ex);
                    }
                    return offset == 0 ? "the log you input which without \"Preparing:\" or \"Parameters:\"" : null;
                });
            } else {
                output.setText(SqlFormatter.format(sql));
                output.setCaretPosition(0);
            }
        }
    },
    JAVA_BEAN("JavaBean", null),
    JAVA_BEAN_NULL("JustField", ConvertType.JAVA_BEAN) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String javaBean = JsonUtil.jsonToObject(input.getText());
                output.setText(javaBean);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    JAVA_BEAN_SETGET("SetGet", ConvertType.JAVA_BEAN) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String javaBean = JsonUtil.jsonToObject(input.getText(), true, false);
                output.setText(javaBean);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    JAVA_BEAN_DOC("Doc", ConvertType.JAVA_BEAN) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String javaBean = JsonUtil.jsonToObject(input.getText(), false, true);
                output.setText(javaBean);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    JAVA_BEAN_ALL("SetGetDoc", ConvertType.JAVA_BEAN) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            try {
                String javaBean = JsonUtil.jsonToObject(input.getText(), true, true);
                output.setText(javaBean);
                output.setCaretPosition(0);
            } catch (Exception ex) {
                setJsonSyntaxException(input, ex);
            }
        }
    },
    TEXT("Text", null),
    TEXT_CAMELCASE("CamelCase", ConvertType.TEXT) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String text = Arrays.stream(input.getText().split("\n")).map(s -> StringUtil.textToCamelCase(s, false)).collect(Collectors.joining("\n"));
            output.setText(text);
            output.setCaretPosition(0);
        }
    },
    TEXT_CONSTANT("Constant", ConvertType.TEXT) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String text = Arrays.stream(input.getText().split("\n")).map(StringUtil::textToConstant).collect(Collectors.joining("\n"));
            output.setText(text);
            output.setCaretPosition(0);
        }
    },
    TEXT_KEBABCASE("KebabCase", ConvertType.TEXT) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String text = Arrays.stream(input.getText().split("\n")).map(s -> StringUtil.textToKebabCase(s, false)).collect(Collectors.joining("\n"));
            output.setText(text);
            output.setCaretPosition(0);
        }
    },
    TEXT_UNDERSCORECASE("UnderscoreCase", ConvertType.TEXT) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String text = Arrays.stream(input.getText().split("\n")).map(s -> StringUtil.textToUnderscoreCase(s, false)).collect(Collectors.joining("\n"));
            output.setText(text);
            output.setCaretPosition(0);
        }
    },
    TEXT_WORD("Word", ConvertType.TEXT) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String text = Arrays.stream(input.getText().split("\n")).map(StringUtil::textToWords).collect(Collectors.joining("\n"));
            output.setText(text);
            output.setCaretPosition(0);
        }
    },
    TEXT_TRANSLATOR("Translator", ConvertType.TEXT) {
        @Override
        public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
            String text = Arrays.stream(input.getText().split("\n")).map(TranslatorFactroy::translate).collect(Collectors.joining("\n"));
            output.setText(text);
            output.setCaretPosition(0);
        }
    },
    ;
    private static final Pattern ERROR_PATTERN = Pattern.compile("line (\\w+) column");

    String name;
    ConvertType type;

    ToolMenu(String name, ConvertType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ConvertType getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
        if (this.type == null) {
            return;
        }
        // 默认对应类型格式化
        this.type.handle(input, output);
    }


    private static void setJsonSyntaxException(RSyntaxTextArea input, Exception ex) {
        String msg = ex.getMessage();
        Matcher matcher = ERROR_PATTERN.matcher(msg);
        if (matcher.find()) {
            // 行索引从0开始
            int lineIndex = (Integer.parseInt(matcher.group(1))) - 1;
            // 设置错误行背景色
            try {
                input.addLineHighlight(lineIndex, JBColor.RED);
            } catch (BadLocationException e) {
                NoticeUtil.error(e);
            }
            // 设置提示信息
            input.setToolTipSupplier((RTextArea rt, MouseEvent me) -> {
                int offset = 0;
                try {
                    offset = input.getLineOfOffset(input.viewToModel(me.getPoint()));
                } catch (BadLocationException e) {
                    NoticeUtil.error(e);
                }
                return offset == lineIndex ? msg : null;
            });
            // 定位到失败行
            input.setCaretPosition(lineIndex);
            NoticeUtil.init("Json Format");
        }
        NoticeUtil.error(msg, ex);
    }

}
