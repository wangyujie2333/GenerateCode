package com.idea.plugin.setting;

import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.report.service.ReportGenerator;
import com.idea.plugin.setting.support.*;
import com.idea.plugin.utils.JsonUtil;
import com.idea.plugin.utils.ThreadLocalUtils;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ToolSettings", storages = @Storage("pluginCreateFile.xml"))
public class ToolSettings implements PersistentStateComponent<ToolsConfigVO> {

    private ToolsConfigVO config = new ToolsConfigVO();

    public static ToolSettings getInstance(Project project) {
        return ServiceManager.getService(project, ToolSettings.class);
    }

    @Nullable
    @Override
    public ToolsConfigVO getState() {
        if (config == null) {
            config = new ToolsConfigVO();
        }
        return config;
    }

    @Override
    public void loadState(@NotNull ToolsConfigVO config) {
        this.config = config;
    }


    public static ToolsConfigVO getConfig() {
        Project project;
        ActionContext context = ThreadLocalUtils.get(ActionContext.class, "ActionContext");
        if (context != null) {
            project = context.getProject();
        } else {
            project = ActionContext.globalproject;
        }
        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }
        ToolSettings instance = getInstance(project);
        if (instance == null) {
            return new ToolSettings().getState();
        }
        return instance.getState();
    }

    public static CopyBuildConfigVO getCopyBuildConfig() {
        ToolsConfigVO config = getConfig();
        if (config.copyBuildConfigVO == null) {
            config.copyBuildConfigVO = new CopyBuildConfigVO();
        }
        return config.copyBuildConfigVO;
    }

    public static ReportConfigVO getReportConfig() {
        ToolsConfigVO config = getConfig();
        if (config.reportConfigVO == null || config.reportConfigVO.getOpen() == null) {
            ReportGenerator reportGenerator = new ReportGenerator();
            String reportConfig = reportGenerator.getReportConfig();
            config.reportConfigVO = JsonUtil.fromJson(reportConfig, ReportConfigVO.class);
        }
        return config.reportConfigVO;
    }

    public static SettingConfigVO getSettingConfig() {
        ToolsConfigVO config = getConfig();
        if (config.settingConfigVO == null) {
            config.settingConfigVO = new SettingConfigVO();
        }
        return config.settingConfigVO;
    }

    public static TableConfigVO getTableConfig() {
        ToolsConfigVO config = getConfig();
        if (config.tableConfigVO == null) {
            config.tableConfigVO = new TableConfigVO();
        }
        return config.tableConfigVO;
    }

    public static TranslateConfigVO getTranslateConfig() {
        ToolsConfigVO config = getConfig();
        if (config.translateConfigVO == null) {
            config.translateConfigVO = new TranslateConfigVO();
        }
        return config.translateConfigVO;
    }

    public static JavaFileConfigVO getJavaFileConfig() {
        ToolsConfigVO config = getConfig();
        if (config.javaFileConfigVO == null) {
            config.javaFileConfigVO = new JavaFileConfigVO();
        }
        return config.javaFileConfigVO;
    }

}
