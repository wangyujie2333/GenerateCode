package com.idea.plugin.orm.service;

import com.idea.plugin.orm.support.FileTypeInfo;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.TableModuleFactory;
import com.idea.plugin.orm.support.enums.FileTypeEnum;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.setting.template.JavaTemplateVO;
import com.idea.plugin.sql.support.GeneralOrmInfoVO;
import com.idea.plugin.utils.FileUtils;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;


public class ProjectGenerator extends GeneratorConfig {

    public static void generationFile(GeneratorContext context) {
        try {
            getFileTypeInfo(context);
            TableModuleFactory.createTableModule(context);
            FileUtils.writeFileDelete(context.getFileTypeInfo().getAbsulotePath(), getTemplate(context.getTableModule(), context.getFileType().getFtlpath()));
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public static void getFileTypeInfo(GeneratorContext context) {
        GeneralOrmInfoVO generalOrmInfoVO = context.getGeneralOrmInfoVO();
        FileTypePathEnum fileTypePathEnum = context.getFileType();
        FileTypeInfo fileTypeInfo = new FileTypeInfo();
        context.setFileTypeInfo(fileTypeInfo);
        String modulePath = generalOrmInfoVO.modulePath.trim();
        if (modulePath.lastIndexOf("/") == modulePath.length() - 1) {
            modulePath = modulePath.substring(0, modulePath.lastIndexOf("/"));
        }
        String moduleFullName = modulePath.substring(modulePath.lastIndexOf("/") + 1);
        File file = new File(modulePath);
        if (!file.exists() && !file.isDirectory()) {
            throw new RuntimeException(String.format("模块路径：%s不存在", modulePath));
        }
        FileTypeEnum fileType = fileTypePathEnum.getFileType();
        String subModulePath = moduleFullName.toLowerCase().replaceAll("\\.", "/");
        String moduleName = moduleFullName;
        if (moduleFullName.lastIndexOf(".") > 0) {
            moduleName = moduleFullName.substring(moduleFullName.lastIndexOf(".") + 1);
        }
        String packgPath = fileTypePathEnum.getJavapath(generalOrmInfoVO);
        String fileName = null;
        ReportConfigVO reportConfig = ToolSettings.getReportConfig();
        if (reportConfig.getJavaTemplateVO() != null) {
            context.setJavaTemplateVO(reportConfig.getJavaTemplateVO());
        }
        JavaTemplateVO javaTemplateVO = context.getJavaTemplateVO();
        if (context.getTableInfoVO() != null) {
            fileName = fileTypePathEnum.getFileName(context.getTableInfoVO().tableName, javaTemplateVO);
        }
        if (context.getClazzInfoVO() != null) {
            context.getClazzInfoVO().setPackageName(packgPath);
            if (StringUtils.isEmpty(fileName)) {
                fileName = fileTypePathEnum.getClazzFileName(context.getClazzInfoVO().clazzName, javaTemplateVO);
            }
            if (StringUtils.isNotEmpty(fileName)) {
                context.getClazzInfoVO().setClazzName(fileName);
                if (context.getClazzInfoVO().getMethodInfos().stream().anyMatch(methodInfoVO ->
                        context.getClazzInfoVO().getImportList().stream().noneMatch(s -> s.endsWith("DO"))
                                && methodInfoVO.getMethodParameter().values().stream().anyMatch(s -> s.endsWith("DO")))) {
                    context.getClazzInfoVO().addImport(getImprotStr(context, generalOrmInfoVO, FileTypePathEnum.DO, fileName));
                }
                if (context.getClazzInfoVO().getMethodInfos().stream().anyMatch(methodInfoVO ->
                        context.getClazzInfoVO().getImportList().stream().noneMatch(s -> s.endsWith("VO"))
                                && methodInfoVO.getMethodParameter().values().stream().anyMatch(s -> s.endsWith("VO")))) {
                    context.getClazzInfoVO().addImport(getImprotStr(context, generalOrmInfoVO, FileTypePathEnum.VO, fileName));
                }
                if (FileTypePathEnum.DAO.equals(fileTypePathEnum)) {
                    context.getClazzInfoVO().setResourceClazz(getImprotStr(context, generalOrmInfoVO, FileTypePathEnum.DAO, fileName));
                    if (JavaTemplateVO.isJpa(context.getJavaTemplateVO())) {
                        context.getClazzInfoVO().addImport("org.springframework.data.jpa.repository.JpaRepository");
                        context.getClazzInfoVO().addImport("org.springframework.data.jpa.repository.JpaSpecificationExecutor");
                    }
                    context.getClazzInfoVO().addImport("org.springframework.stereotype.Repository");
                }
                if (FileTypePathEnum.SERVICE.equals(fileTypePathEnum)) {
                    context.getClazzInfoVO().setResourceClazz(FileTypePathEnum.DAO.getClazzFileName(fileName, javaTemplateVO));
                    context.getClazzInfoVO().setImplClazz(FileTypePathEnum.ISERVICE.getClazzFileName(fileName, javaTemplateVO));
                    context.getClazzInfoVO().addImport(getImprotStr(context, generalOrmInfoVO, FileTypePathEnum.DAO, fileName));
                    context.getClazzInfoVO().addImport(getImprotStr(context, generalOrmInfoVO, FileTypePathEnum.ISERVICE, fileName));
                    context.getClazzInfoVO().addImport("org.springframework.stereotype.Service");
                    context.getClazzInfoVO().addImport("org.springframework.beans.factory.annotation.Autowired");
                }
                if (FileTypePathEnum.CONTROLLER.equals(fileTypePathEnum)) {
                    context.getClazzInfoVO().setResourceClazz(FileTypePathEnum.ISERVICE.getClazzFileName(fileName, javaTemplateVO));
                    context.getClazzInfoVO().addImport(getImprotStr(context, generalOrmInfoVO, FileTypePathEnum.ISERVICE, fileName));
                    context.getClazzInfoVO().addImport("org.springframework.web.bind.annotation.RestController");
                    context.getClazzInfoVO().addImport("org.springframework.beans.factory.annotation.Autowired");
                    context.getClazzInfoVO().addImport("org.springframework.web.bind.annotation.*");
                    if (StringUtils.isNotEmpty(context.getGeneralOrmInfoVO().controllerReturn)) {
                        context.getClazzInfoVO().addImport(context.getGeneralOrmInfoVO().controllerReturn);
                    }
                }
            }
        }
        fileTypeInfo.setModulePath(subModulePath);
        fileTypeInfo.setModuleName(moduleName);
        fileTypeInfo.setPackagePath(packgPath);
        fileTypeInfo.setFileName(fileName);
        fileTypeInfo.setFileType(fileType.getType());
        fileTypeInfo.setFileTypePath(fileType.getPath());
        if (javaTemplateVO != null) {
            boolean jpa = JavaTemplateVO.isJpa(context.getJavaTemplateVO());
            if (jpa && fileType.equals(FileTypeEnum.XML)) {
                return;
            }
        }
        VirtualFile virtualFile = FileUtils.createDir(modulePath + "/" + fileType.getPath() + packgPath);
        fileTypeInfo.setAbsulotePath(virtualFile.getPath() + "/" + fileName + "." + fileType.getType());
    }

    public static String getImprotStr(GeneratorContext context, GeneralOrmInfoVO generalOrmInfoVO, FileTypePathEnum fileTypePathEnum, String fileName) {
        return (fileTypePathEnum.getJavapath(generalOrmInfoVO) + "/" + fileTypePathEnum.getClazzFileName(fileName, context.getJavaTemplateVO())).replaceAll("/", ".");
    }

}
