package com.idea.plugin.charreplace;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ChineseCharReplaceHandler extends TypedHandlerDelegate {

    @Override
    public @NotNull Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
        if (StringUtils.isNotBlank(String.valueOf(c))) {
            editor.getDocument().addDocumentListener(new CharReplaceLisener(editor, file));
        }
        return Result.CONTINUE;
    }

}
