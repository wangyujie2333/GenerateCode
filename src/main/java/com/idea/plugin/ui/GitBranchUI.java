package com.idea.plugin.ui;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GitBranchUI extends DialogWrapper {
    private JPanel jPanel;
    private JTextField branchNameField;
    private JRadioButton mfeatureButton;
    private JRadioButton mbugButton;

    public static GitBranchUI getInstance(Project project) {
        return new GitBranchUI(project);
    }

    public GitBranchUI(@Nullable Project project) {
        super(project);
        super.setSize(400, 30);
        super.init();
        setTitle("Create New Branch");
        ReportConfigVO config = ToolSettings.getReportConfig();
        String mfeature = config.getGitSetting().getBranchKey().getMfeature();
        String mbug = config.getGitSetting().getBranchKey().getMbug();
        String pattern = mfeature + "|" + mbug;
        mfeatureButton.addActionListener(e -> {
            mbugButton.setSelected(!mfeatureButton.isSelected());
            setBranchName(mfeature, pattern);
        });
        mbugButton.addActionListener(e -> {
            mfeatureButton.setSelected(!mbugButton.isSelected());
            setBranchName(mbug, pattern);
        });
    }

    public void setBranchName(String type, String pattern) {
        String text = branchNameField.getText();
        text = text.replaceAll(pattern, type);
        setFieldText(text);
    }

    public void setFieldText(String text) {
        branchNameField.setText(text);
    }

    public String getBranchName() {
        return branchNameField.getText();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jPanel;
    }
}
