package com.idea.plugin.image;

import com.idea.plugin.report.service.ReportGenerator;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PasteImageHandler extends EditorActionHandler {
    private final EditorActionHandler myOriginalHandler;

    private static final ReportGenerator reportGenerator = new ReportGenerator();

    public PasteImageHandler(EditorActionHandler originalAction) {
        myOriginalHandler = originalAction;
    }


    private AnActionEvent createAnEvent(AnAction action, @NotNull DataContext context) {
        Presentation presentation = action.getTemplatePresentation().clone();
        return new AnActionEvent(null, context, ActionPlaces.UNKNOWN, presentation, ActionManager.getInstance(), 0);
    }

    @Override
    public void doExecute(@NotNull final Editor editor, Caret caret, final DataContext dataContext) {
        reportGenerator.runSchedule();
        try {
            if (editor instanceof EditorEx && ((EditorEx) editor).getVirtualFile() != null) {
                if (((EditorEx) editor).getVirtualFile().getPath().endsWith(".md")) {
                    List<String> imageNames = ImageUtils.getImageFromClipboard();
                    if (CollectionUtils.isNotEmpty(imageNames)) {
                        PasteImageAnAction action = new PasteImageAnAction(imageNames);
                        AnActionEvent event = createAnEvent(action, dataContext);
                        action.actionPerformed(event);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            NoticeUtil.error(e);
        }
        if (myOriginalHandler != null) {
            myOriginalHandler.execute(editor, null, dataContext);
        }
    }
}
