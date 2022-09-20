package com.idea.plugin.orm;

import com.idea.plugin.ui.CreateJavaFileUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JavaGenerateDialogWrapper extends DialogWrapper {

    private CreateJavaFileUI createJavaFileUI;

    private Project project;
    private String fileType;

    public JavaGenerateDialogWrapper(@Nullable Project project) {
        super(project);
        createJavaFileUI = CreateJavaFileUI.getInstance(project);
        setOKButtonText("确认");
        setCancelButtonText("取消");
        super.init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return createJavaFileUI.getMianPanel();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        fillData(project);
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
        fillData(project);
    }


    public void fillData(Project project) {
        this.project = project;
        createJavaFileUI.fillData(project);
    }

    public void fillData(Project project, boolean fileType) {
        this.project = project;
        createJavaFileUI.fillData(project, fileType);
    }

    public void fillData() {
        createJavaFileUI.fillData();
    }

    public String getProcedureType() {
        return createJavaFileUI.getType();
    }

}
