package com.idea.plugin.popup;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.TranslateConfigVO;
import com.idea.plugin.translator.TranslatorConfig;
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.idea.plugin.utils.StringUtil;
import com.idea.plugin.word.WordTypeEnum;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WordChangeAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            Editor editor = context.getEditor();
            if (editor == null) {
                return;
            }
            SelectionModel selectionModel = editor.getSelectionModel();
            int strartOffset = selectionModel.getSelectionStart();
            int endOffset = selectionModel.getSelectionEnd();
            List<Caret> allCarets = editor.getCaretModel().getAllCarets();
            if (allCarets.size() == 1) {
                String selectedText = selectionModel.getSelectedText();
                if (StringUtils.isEmpty(selectedText)) {
                    selectedText = allCarets.get(0).getSelectedText();
                    if (StringUtils.isEmpty(selectedText) || selectedText.contains("\n")) {
                        return;
                    }
                }

                String translate = TranslatorFactroy.translate(selectedText);
                showPopupBalloon(selectedText, translate, strartOffset, endOffset);
            } else {
                TranslateConfigVO config = ToolSettings.getTranslateConfig();
                WordTypeEnum wordTypeEnum = config.wordTypeEnum;
                for (Caret caret : allCarets) {
                    int sOffset = caret.getSelectionStart();
                    int eOffset = caret.getSelectionEnd();
                    String sText = caret.getSelectedText();
                    if (StringUtils.isEmpty(sText) || sText.contains("\n")) {
                        continue;
                    }
                    if (wordTypeEnum.equals(WordTypeEnum.TRANSLATE)) {
                        sText = TranslatorFactroy.translate(sText);
                    } else if (wordTypeEnum.equals(WordTypeEnum.SNAKE_CASE)) {
                        sText = StringUtil.textToConstant(sText);
                    } else if (wordTypeEnum.equals(WordTypeEnum.UP_CAMEL_CASE)) {
                        sText = StringUtil.textToCamelCase(sText, true);
                    } else if (wordTypeEnum.equals(WordTypeEnum.CAMEL_CASE)) {
                        sText = StringUtil.textToCamelCase(sText, false);
                    } else if (wordTypeEnum.equals(WordTypeEnum.KEBAB_CASE)) {
                        sText = StringUtil.textToKebabCase(sText, false);
                    }
                    replaceStr(sOffset, eOffset, sText);
                }
            }
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.getProject() != null && context.getEditor() != null);
    }

    protected void showPopupBalloon(String selectText, String translate, int strartOffset, int endOffset) {
        ApplicationManager.getApplication().invokeLater(() -> {
            final JBPopupFactory factory = JBPopupFactory.getInstance();
            List<String> resultList = new ArrayList<>();
            String lantype = TranslatorConfig.getLantype(selectText);
            if (selectText.equals(translate)) {
                String nowStr = DateUtils.nowDate(selectText);
                if (nowStr != null) {
                    resultList.add(nowStr);
                } else {
                    if (StringUtil.isNumber(selectText)) {
                        resultList.add(String.valueOf(Long.parseLong(selectText) - 1));
                        resultList.add(selectText);
                        resultList.add(String.valueOf(Long.parseLong(selectText) + 1));
                    }
                }
            } else {
                addWord(resultList, selectText, translate.trim(), lantype);
            }
            String title = selectText;
            if (selectText.length() > 10) {
                title = selectText.substring(0, 10) + "...";
            }
            SelectListStep step = new SelectListStep(title + " 翻译结果", resultList, strartOffset, endOffset);
            step.setDefaultOptionIndex(0);
            ListPopup popup = factory.createListPopup(step, 20);
            popup.setRequestFocus(true);
            popup.show(factory.guessBestPopupLocation(context.getEditor()));
        });
    }

    private void addWord(List<String> list, String selectText, String translate, String translateType) {
        String resultText;
        if (TranslatorConfig.ZH_CN_TO_EN.equals(translateType)) {
            resultText = StringUtil.textToWords(translate, false);
        } else {
            list.add(translate);
            resultText = StringUtil.textToWords(selectText, false);
        }
        list.add(resultText);
        list.addAll(StringUtil.getAllTranslateCase(resultText));
    }

    public static void replaceStr(int strartOffset, int endOffset, String newText) {
        Editor editor = context.getEditor();
        SelectionModel selectionModel = editor.getSelectionModel();
        Runnable runnable = () -> editor.getDocument().replaceString(strartOffset, endOffset, newText);
        WriteCommandAction.runWriteCommandAction(context.getProject(), runnable);
        selectionModel.removeSelection();
    }


    class SelectListStep extends BaseListPopupStep {
        int strartOffset;
        int endOffset;

        public SelectListStep(@Nullable String title, List values) {
            super(title, values);
        }

        public SelectListStep(@Nullable String title, List values, int strartOffset, int endOffset) {
            super(title, values);
            this.strartOffset = strartOffset;
            this.endOffset = endOffset;
        }

        @Nullable
        @Override
        public PopupStep onChosen(Object selectedValue, boolean finalChoice) {
            List values = getValues();
            int i = values.indexOf(selectedValue.toString());
            if (values.size() != WordTypeEnum.values().length) {
                --i;
            }
            WordTypeEnum wordTypeEnum = WordTypeEnum.indexToEnum(i);
            ToolSettings.getTranslateConfig().setWordTypeEnum(wordTypeEnum);
            replaceStr(strartOffset, endOffset, selectedValue.toString());
            return super.onChosen(selectedValue, finalChoice);
        }

        @Override
        public boolean isSpeedSearchEnabled() {
            return true;
        }
    }
}
