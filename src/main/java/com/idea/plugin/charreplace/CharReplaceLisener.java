package com.idea.plugin.charreplace;

import com.idea.plugin.charreplace.support.SupportFileTypeEnum;
import com.idea.plugin.report.service.ReportGenerator;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.SettingConfigVO;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.DocumentUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CharReplaceLisener implements DocumentListener {

    private Editor myEditor;
    private PsiFile myFile;
    private static final ReportGenerator reportGenerator = new ReportGenerator();


    public CharReplaceLisener(Editor editor, PsiFile file) {
        this.myEditor = editor;
        this.myFile = file;
    }


    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        DocumentListener.super.beforeDocumentChange(event);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        this.documentChanged(event, this.myEditor);
    }

    public void documentChanged(@NotNull DocumentEvent event, Editor editor) {
        reportGenerator.runSchedule();
        SettingConfigVO config = ToolSettings.getSettingConfig();
        if (config.replace) {
            if (isCanReplace(event, editor)) {
                String[] replaceTextArr = config.replaceText.split("\\|");
                Map<String, String> replaceTextMap = new HashMap<>();
                for (String replaceText : replaceTextArr) {
                    replaceText.replaceAll(" ", "");
                    if (replaceText.length() == 2) {
                        replaceTextMap.put(String.valueOf(replaceText.charAt(0)), String.valueOf(replaceText.charAt(1)));
                    }
                }
                String originalText = event.getNewFragment().toString();
                if (replaceTextMap.containsKey(originalText)) {
                    String replacement = replaceTextMap.get(originalText);
                    replace(event, editor, replacement);
                }
            }
        }
        event.getDocument().removeDocumentListener(CharReplaceLisener.this);
    }

    private boolean isCanReplace(@NotNull DocumentEvent event, Editor editor) {
        if (myEditor == null) {
            return false;
        }
        if (event.getNewLength() > 1 || !(myEditor instanceof EditorImpl)) {
            return false;
        }
        final CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        PsiElement element = this.myFile.findElementAt(caretOffset);
        PsiComment parentOfType = PsiTreeUtil.getParentOfType(element, PsiComment.class, false);
        if (parentOfType != null) {
            return false;
        }
        SupportFileTypeEnum supportFileType = SupportFileTypeEnum.codeToEnum(myFile.getFileType().getName().toLowerCase());
        if (supportFileType == null) {
            return false;
        }
        int lineStartOffset = DocumentUtil.getLineStartOffset(caretOffset, editor.getDocument());
        int lineEndOffset = DocumentUtil.getLineEndOffset(caretOffset, editor.getDocument());
        String line = editor.getDocument().getText(TextRange.create(lineStartOffset, lineEndOffset));
        String[] commentStart = supportFileType.getCommentStart();
        String[] commentEnd = supportFileType.getCommentEnd();
        boolean isComment = false;
        if (commentStart != null) {
            isComment = StringUtils.endsWithAny(StringUtils.trim(line), commentEnd);
            if (!isComment) {
                isComment = StringUtils.startsWithAny(StringUtils.trim(line), commentStart);
            }
        }
        return !isComment;
    }

    private void replace(@NotNull DocumentEvent event, Editor editor, String replacement) {
        Document document = event.getDocument();
        Project project = editor.getProject();
        int currentOffset = event.getOffset() + event.getNewLength();
        ApplicationManager.getApplication().invokeLater(() -> {
            if (editor.isDisposed()) {
                return;
            }
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.replaceString(event.getOffset(), currentOffset, replacement);
//                HintManagerImpl hintManager = (HintManagerImpl) HintManagerImpl.getInstance();
//                JComponent label = HintUtil.createInformationLabel("text", null, null, null);
//                if (!ApplicationManager.getApplication().isUnitTestMode()) {
//                    AccessibleContextUtil.setName(label, "Hint");
//                    LightweightHint hint = new LightweightHint(label);
//                    Point p = HintManagerImpl.getHintPosition(hint, editor, editor.getCaretModel().getVisualPosition(), (short) 1);
//                    hintManager.showEditorHint(hint, editor, p, 12, 0, true, (short) 1);
//                }
            });
        });
    }


    @Override
    public void bulkUpdateStarting(@NotNull Document document) {
        DocumentListener.super.bulkUpdateStarting(document);
    }

    @Override
    public void bulkUpdateFinished(@NotNull Document document) {
        DocumentListener.super.bulkUpdateFinished(document);
    }

}
