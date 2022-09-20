package com.idea.plugin.window;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.SettingConfigVO;
import com.idea.plugin.setting.support.TranslateConfigVO;
import com.idea.plugin.ui.SettingUI;
import com.idea.plugin.utils.JsonUtil;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class SettingFactory implements SearchableConfigurable {
    private static SettingUI settingUI;
    private final Project project;

    public SettingFactory(Project project) {
        this.project = project;
        settingUI = SettingUI.getInstance(project);
    }

    @Override
    public @NotNull
    String getId() {
        return "Create.File";
    }

    @Override
    public @NotNull
    String getDisplayName() {
        return "Create File";
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        return settingUI.getMianPanel();
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        SettingConfigVO config = ToolSettings.getSettingConfig();
        settingUI.tokenText.setText(config.token);
        config.replace = settingUI.replaceCheckBox.isSelected();
        config.replaceText = settingUI.replaceText.getText();
        config.translate = settingUI.translateComboBox.getSelectedItem().toString();
        config.appId = settingUI.appIdText.getText();
        config.token = settingUI.tokenText.getText();
        config.setAuthor(settingUI.authorText.getText());
        Map<String, String> wordTransMap = new HashMap<>();
        if (StringUtils.isNotEmpty(settingUI.wordJsonText.getText())) {
            String wordJson = Arrays.stream(settingUI.wordJsonText.getText().split(";")).map(String::trim).collect(Collectors.joining("\n"));
            wordJson = JsonUtil.propertyToJson(wordJson);
            wordTransMap = JsonUtil.fromJson(wordJson, Map.class);
        }
        TranslateConfigVO translateConfig = ToolSettings.getTranslateConfig();
        translateConfig.setWordJsonMap(wordTransMap);
        if (MapUtils.isNotEmpty(wordTransMap)) {
            translateConfig.cacheMap.putAll(wordTransMap);
            wordTransMap.forEach((key, value) -> translateConfig.cacheMap.put(value, key));
        }
    }
}
