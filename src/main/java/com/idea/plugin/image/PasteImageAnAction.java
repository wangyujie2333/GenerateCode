package com.idea.plugin.image;

import com.idea.plugin.popup.BaseAction;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class PasteImageAnAction extends BaseAction {

    private List<String> imageNames;

    public PasteImageAnAction(List<String> imageNames) {
        this.imageNames = imageNames;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            Editor editor = e.getData(PlatformDataKeys.EDITOR);
            if (editor == null) {
                return;
            }
            for (String imageName : imageNames) {
                insertImageElement(editor, imageName);
            }
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    private void insertImageElement(final @NotNull Editor editor, String imageurl) {
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            EditorModificationUtil.insertStringAtCaret(editor, imageurl);
        });
    }

}
