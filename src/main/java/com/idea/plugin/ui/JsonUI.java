package com.idea.plugin.ui;

import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.utils.ClipboardUtils;
import com.idea.plugin.utils.JsonUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JsonUI extends DialogWrapper {
    private JPanel jPanel;
    private JTextArea jsonTextArea;
    private JButton objectButton;
    private JButton jsonButton;
    private JButton prettyButton;
    private JButton propertyButton;

    private ActionContext context;
    private Boolean isJsonFile = false;
    private Boolean isFileSelect = false;
    private int strartOffset;
    private int endOffset;

    public static JsonUI getInstance(Project project) {
        return new JsonUI(project);
    }

    public JsonUI(@Nullable Project project) {
        super(project);
        super.setSize(800, 800);
        setOKButtonText("Copy");
        setCancelButtonText("Cancel");
        super.init();
        isFileSelect = false;
        isJsonFile = false;
        jsonTextArea.setLineWrap(true);
        jsonTextArea.setWrapStyleWord(true);
        objectButton.addActionListener(e -> {
            if (JsonUtil.isJson(jsonTextArea.getText())) {
                jsonTextArea.setText(JsonUtil.jsonToObject(jsonTextArea.getText()));
            } else if (JsonUtil.isProperty(jsonTextArea.getText())) {
                String json = JsonUtil.propertyToJson(jsonTextArea.getText());
                jsonTextArea.setText(JsonUtil.jsonToObject(json));
            }
        });
        propertyButton.addActionListener(e -> {
            if (JsonUtil.isJson(jsonTextArea.getText())) {
                jsonTextArea.setText(JsonUtil.jsonToProperty(jsonTextArea.getText()));
            } else if (JsonUtil.isObject(jsonTextArea.getText())) {
                String json = JsonUtil.objectToJson(jsonTextArea.getText());
                jsonTextArea.setText(JsonUtil.jsonToProperty(json));
            }
        });
        jsonButton.addActionListener(e -> {
            if (JsonUtil.isJson(jsonTextArea.getText())) {
                jsonTextArea.setText(JsonUtil.toJson(jsonTextArea.getText()));
            } else if (JsonUtil.isObject(jsonTextArea.getText())) {
                jsonTextArea.setText(JsonUtil.objectToJson(jsonTextArea.getText()));
            } else if (JsonUtil.isProperty(jsonTextArea.getText())) {
                jsonTextArea.setText(JsonUtil.propertyToJson(jsonTextArea.getText()));
            }
        });
        prettyButton.addActionListener(e -> {
            if (JsonUtil.isJson(jsonTextArea.getText())) {
                jsonTextArea.setText(JsonUtil.prettyJson(jsonTextArea.getText()));
            } else if (JsonUtil.isObject(jsonTextArea.getText())) {
                String json = JsonUtil.objectToJson(jsonTextArea.getText());
                jsonTextArea.setText(JsonUtil.prettyJson(json));
            } else if (JsonUtil.isProperty(jsonTextArea.getText())) {
                String json = JsonUtil.propertyToJson(jsonTextArea.getText());
                jsonTextArea.setText(JsonUtil.prettyJson(json));
            }
        });
    }

    public void replaceStr() {
        Editor editor = context.getEditor();
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            Runnable runnable = () -> editor.getDocument().replaceString(strartOffset, endOffset, jsonTextArea.getText());
            WriteCommandAction.runWriteCommandAction(context.getProject(), runnable);
            selectionModel.removeSelection();
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return jPanel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (isJsonFile) {
            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                context.getDocument().setText(jsonTextArea.getText());
                context.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
            });
        } else if (isFileSelect) {
            replaceStr();
        }
        ClipboardUtils.setClipboardText(jsonTextArea.getText());
    }

    public void setJsonFile(Boolean jsonFile) {
        isJsonFile = jsonFile;
    }

    public void setContext(ActionContext context) {
        this.context = context;
    }

    public void setFileSelect(Boolean fileSelect) {
        isFileSelect = fileSelect;
    }

    public void setStrartOffset(int strartOffset) {
        this.strartOffset = strartOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public void setJsonTextArea(String json) {
        this.jsonTextArea.setText(json);
    }
}
