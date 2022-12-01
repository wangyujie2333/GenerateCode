package com.idea.plugin.ui;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.SettingConfigVO;
import com.idea.plugin.setting.support.TranslateConfigVO;
import com.idea.plugin.translator.TranslatorConfig;
import com.idea.plugin.word.WordTypeEnum;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SettingUI {
    public JPanel mianPanel;
    public JTextField replaceText;
    public JCheckBox replaceCheckBox;
    public JComboBox translateComboBox;
    public JTextField appIdText;
    public JTextField tokenText;
    public JTextField authorText;
    public JPanel docPanel;
    public JPanel transPanel;
    public JLabel appIdLabel;
    public JLabel tokenLabel;
    public JButton removeTranslate;
    public JTextArea wordJsonText;

    public static SettingUI getInstance(Project project) {
        return new SettingUI(project);
    }

    public SettingUI(Project project) {
        SettingConfigVO settingConfigVO = ToolSettings.getSettingConfig();
        this.replaceCheckBox.setSelected(settingConfigVO.replace);
        this.replaceText.setText(settingConfigVO.replaceText);
        this.translateComboBox.setSelectedItem(settingConfigVO.translate);
        setVisible(settingConfigVO.translate);
        this.appIdText.setText(settingConfigVO.appId);
        this.tokenText.setText(settingConfigVO.token);
        this.authorText.setText(settingConfigVO.getAuthor());
        TranslateConfigVO translateConfig = ToolSettings.getTranslateConfig();
        Map<String, String> wordJsonMap = translateConfig.getWordJsonMap();
        String wodJson = "";
        if (wordJsonMap != null) {
            List<String> wordJsonList = wordJsonMap.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.toList());
            int len = 0;
            for (String word : wordJsonList) {
                word = word + "; ";
                if (len > 70) {
                    len = 0;
                    word = word + "\n";
                }
                wodJson = wodJson + word;
                len = len + word.length();
            }
        }
        this.wordJsonText.setText(wodJson);
        removeTranslate.addActionListener(e -> {
            translateConfig.wordTypeEnum = WordTypeEnum.TRANSLATE;
            translateConfig.cacheMap = new ConcurrentHashMap<>();
            ToolSettings.getReportConfig().setOpen(null);
            Messages.showMessageDialog("success", "正确", Messages.getInformationIcon());
        });
        replaceCheckBox.addActionListener(e -> settingConfigVO.replace = replaceCheckBox.isSelected());
        replaceText.addActionListener(e -> settingConfigVO.replaceText = replaceText.getText());
        translateComboBox.addActionListener(e -> {
            JComboBox<?> jComboBox = (JComboBox<?>) e.getSource();
            settingConfigVO.translate = jComboBox.getSelectedItem().toString();
            setVisible(jComboBox.getSelectedItem());
        });

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void setVisible(Object translate) {
        if (TranslatorConfig.BAIDU_TRANSLATOR.equals(translate)) {
            appIdLabel.setVisible(true);
            tokenLabel.setVisible(true);
            appIdText.setVisible(true);
            tokenText.setVisible(true);
        } else {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            appIdText.setVisible(false);
            tokenText.setVisible(false);
        }
    }

    public JComponent getMianPanel() {
        return mianPanel;
    }

}
