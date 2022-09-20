package com.idea.plugin.popup;

import com.idea.plugin.demo.DemoGenerateDialogWrapper;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.JavaFileConfigVO;
import com.idea.plugin.setting.support.TableConfigVO;
import com.idea.plugin.utils.JsonUtil;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DemoFilePopupAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            Project project = context.getProject();
            readDemoConfigByText(context);
            String selectFilePath = context.getPsiFile().getViewProvider().getVirtualFile().getParent().getPath();
            DemoGenerateDialogWrapper classGenerateDialogWrapper = new DemoGenerateDialogWrapper(project);
            classGenerateDialogWrapper.fillData(project, selectFilePath);
            classGenerateDialogWrapper.show();
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    public void readDemoConfigByText(ActionContext context) {
        TableConfigVO config = ToolSettings.getTableConfig();
        List<String> textList = context.getTextList();
        if (config == null || CollectionUtils.isEmpty(textList)) {
            return;
        }
        List<String> filterTextList = new ArrayList<>();
        for (String text : textList) {
            if (StringUtils.isEmpty(text) || text.startsWith("#") || text.startsWith("--")) {
                continue;
            }
            text = text.replaceAll("，", ",");
            text = text.replaceAll("；", ";");
            text = text.replaceFirst("：", ":");
            text = text.replaceAll("\\\\", "/");
            if (text.startsWith("procedureType")) {
                filterTextList.add(text.replace("procedureType", "procedureTypeList"));
                filterTextList.add("procedureTypeList:");
                break;
            }
            filterTextList.add(text);
        }
        TableConfigVO tableConfigVO = JsonUtil.fromJson(JsonUtil.getJsonStrByFileStr(filterTextList), TableConfigVO.class);
        List<String> procedureTypeList = new ArrayList<>();
        if (tableConfigVO != null && CollectionUtils.isNotEmpty(tableConfigVO.getProcedureTypeList())) {
            tableConfigVO.getProcedureTypeList().forEach(s -> {
                List<String> procedureTypes = Arrays.stream(s.split(",")).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                procedureTypeList.addAll(procedureTypes);
            });
            tableConfigVO.setProcedureTypeList(procedureTypeList);
        }
        config.copy(tableConfigVO);
        JavaFileConfigVO javaFileConfig = ToolSettings.getJavaFileConfig();
        JavaFileConfigVO javaFileConfigVO = JsonUtil.fromJson(JsonUtil.getJsonStrByFileStr(filterTextList), JavaFileConfigVO.class);
        javaFileConfig.copy(javaFileConfigVO);
        javaFileConfig.copy(config);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

}
