package com.idea.plugin.demo;

import com.idea.plugin.ui.CreateDemoFileUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DemoGenerateDialogWrapper extends DialogWrapper {

    private CreateDemoFileUI createDemoFileUI;

    private Project project;
    private String selectFilePath;

    public DemoGenerateDialogWrapper(@Nullable Project project) {
        super(project);
        createDemoFileUI = CreateDemoFileUI.getInstance(project);
        setOKButtonText("确认");
        setCancelButtonText("取消");
        super.init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return createDemoFileUI.getMianPanel();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        fillData(project, selectFilePath);
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
        fillData(project, selectFilePath);
    }


    public void fillData(Project project, String selectFilePath) {
        this.project = project;
        this.selectFilePath = selectFilePath;
        createDemoFileUI.fillData(project, selectFilePath);
    }

}
