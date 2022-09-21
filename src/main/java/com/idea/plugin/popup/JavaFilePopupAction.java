package com.idea.plugin.popup;

import com.idea.plugin.orm.JavaGenerateDialogWrapper;
import com.idea.plugin.orm.service.ProjectGenerator;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.JavaFileConfigVO;
import com.idea.plugin.sql.support.GeneralOrmInfoVO;
import com.idea.plugin.sql.support.TableOrmInfoVO;
import com.idea.plugin.utils.ActionUtils;
import com.idea.plugin.utils.CreateFileUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class JavaFilePopupAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            GeneralOrmInfoVO generalOrmInfoVO = new GeneralOrmInfoVO();
            if (context.isPsiFilePathSuffix("txt")) {
                generalOrmInfoVO = ActionUtils.readGeneralInfoByText(context, GeneralOrmInfoVO.class);
                CreateFileUtils.generatorJavaFile(generalOrmInfoVO, context);
                NoticeUtil.info("文件创建成功, 路径: " + generalOrmInfoVO.modulePath);
            } else {
                Project project = context.getProject();
                JavaGenerateDialogWrapper javaGenerateDialogWrapper = new JavaGenerateDialogWrapper(project);
                javaGenerateDialogWrapper.fillData(project, context.isPsiFilePathSuffix("java"));
                if (javaGenerateDialogWrapper.showAndGet()) {
                    TableOrmInfoVO tableOrmInfoVO = new TableOrmInfoVO();
                    tableOrmInfoVO.setProcedureType(javaGenerateDialogWrapper.getProcedureType());
                    JavaFileConfigVO javaFileConfig = ToolSettings.getJavaFileConfig();
                    generalOrmInfoVO.setModulePath(javaFileConfig.getModulePath());
                    generalOrmInfoVO.setDoPath(javaFileConfig.getDoPath());
                    generalOrmInfoVO.setVoPath(javaFileConfig.getVoPath());
                    generalOrmInfoVO.setDaoPath(javaFileConfig.getDaoPath());
                    generalOrmInfoVO.setServicePath(javaFileConfig.getServicePath());
                    generalOrmInfoVO.setIservicePath(javaFileConfig.getIservicePath());
                    generalOrmInfoVO.setControllerPath(javaFileConfig.getControllerPath());
                    generalOrmInfoVO.setControllerReturn(javaFileConfig.getControllerReturn());
                    generalOrmInfoVO.setAuthor(ToolSettings.getSettingConfig().getAuthor());
                    generalOrmInfoVO.setMethods(javaFileConfig.getMethods());
                    generalOrmInfoVO.setTableInfos(Collections.singletonList(tableOrmInfoVO));
                    ProjectGenerator projectGenerator = new ProjectGenerator();
                    String interfaceClazz = Arrays.stream(context.getVirtualFiles()).filter(it -> it.getPath().endsWith(("java")))
                            .map(virtualFile -> virtualFile.getName().substring(0, virtualFile.getName().lastIndexOf("."))).collect(Collectors.joining(";"));
                    if (StringUtils.isNotEmpty(interfaceClazz)) {
                        tableOrmInfoVO.setInterfaceClazz(interfaceClazz);
                        CreateFileUtils.generalFromFile(generalOrmInfoVO, context, projectGenerator, tableOrmInfoVO);
                        NoticeUtil.info("文件创建成功, 路径: " + generalOrmInfoVO.modulePath);
                    } else if (context.getPsiElements().length > 0) {
                        List<DbTable> dbTables = Arrays.stream(context.getPsiElements()).filter(it -> it instanceof DbTable).map(it -> (DbTable) it).collect(Collectors.toList());
                        for (DbTable dbTable : dbTables) {
                            Class<GeneralOrmInfoVO> generalOrmInfoVOClass = GeneralOrmInfoVO.class;
                            Class<TableOrmInfoVO> tableOrmInfoVOClass = TableOrmInfoVO.class;
                            CreateFileUtils.generalFromDbTable(generalOrmInfoVO, projectGenerator, generalOrmInfoVOClass, tableOrmInfoVOClass, dbTable);
                        }
                        NoticeUtil.info("文件创建成功, 路径: " + generalOrmInfoVO.modulePath);
                    }
                }
            }
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.isPsiFilePathSuffix("txt")
                || context.isPsiFilePathSuffix("java")
                || Optional.ofNullable(context.getVirtualFiles())
                .map(Arrays::stream)
                .map(s -> s.anyMatch(it -> it.getPath().endsWith(("java"))))
                .orElse(false)
                || Optional.ofNullable(context.getPsiElements())
                .map(Arrays::stream)
                .map(s -> s.anyMatch(it -> it instanceof DbTable))
                .orElse(false));
    }

}
