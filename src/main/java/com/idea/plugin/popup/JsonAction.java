package com.idea.plugin.popup;

import com.idea.plugin.text.JsonFormatService;
import com.idea.plugin.text.TextFormatView;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class JsonAction extends BaseAction {

    private static final JsonFormatService jsonFormatService = new JsonFormatService();

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);
        try {
            TextFormatView instance = TextFormatView.getInstance(context.getProject());
            instance.inputTextArea.setText(jsonFormatService.doGenerate(context, instance));
            instance.inputTextArea.setCaretPosition(0);
            instance.showframe();
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(true);
    }
}
