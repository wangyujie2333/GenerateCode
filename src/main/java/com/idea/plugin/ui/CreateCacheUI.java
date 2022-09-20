package com.idea.plugin.ui;

import com.idea.plugin.setting.ToolSettings;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

public class CreateCacheUI {
    private JPanel mainPanel;

    private JButton removeDemoFileCache;
    private JButton removeCopyFileCache;

    public CreateCacheUI() {
        removeDemoFileCache.addActionListener(e -> {
            ToolSettings.getTableConfig().tabNameCacheMap = new ConcurrentHashMap<>();
            ToolSettings.getTableConfig().tableInfoCacheMap = new ConcurrentHashMap<>();
            Messages.showMessageDialog("success", "正确", Messages.getInformationIcon());
        });
        removeCopyFileCache.addActionListener(e -> {
            ToolSettings.getCopyBuildConfig().filePathCache = null;
            Messages.showMessageDialog("success", "正确", Messages.getInformationIcon());
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public JComponent getMainPanel() {
        return mainPanel;
    }
}
