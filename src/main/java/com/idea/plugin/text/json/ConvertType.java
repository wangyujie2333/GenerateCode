package com.idea.plugin.text.json;

import com.idea.plugin.utils.NoticeUtil;
import com.intellij.ui.JBColor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;

import javax.swing.text.BadLocationException;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum ConvertType {

    JSON(SyntaxConstants.SYNTAX_STYLE_JSON),
    SQL(SyntaxConstants.SYNTAX_STYLE_SQL),
    JAVA_BEAN(SyntaxConstants.SYNTAX_STYLE_JAVA),
    YAML(SyntaxConstants.SYNTAX_STYLE_YAML),
    TEXT(SyntaxConstants.SYNTAX_STYLE_NONE);

    String style;

    ConvertType(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    private static final Pattern ERROR_PATTERN = Pattern.compile("line (\\w+) column");

    /**
     * 对文本进行格式化
     *
     * @param input  输入文本域
     * @param output 输出文本域
     */
    public void handle(RSyntaxTextArea input, RSyntaxTextArea output) {
        // 默认不做任何处理
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
