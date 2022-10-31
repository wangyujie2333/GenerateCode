package com.idea.plugin.orm.support;

import com.idea.plugin.document.support.ClazzInfoVO;
import com.idea.plugin.orm.support.enums.ClazzTypeEnum;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.setting.template.JavaTemplateVO;
import com.idea.plugin.sql.support.GeneralOrmInfoVO;
import com.idea.plugin.sql.support.TableInfoVO;
import com.intellij.openapi.project.Project;

public class GeneratorContext {
    private Project project;
    private GeneralOrmInfoVO generalOrmInfoVO;
    private FileTypePathEnum fileType;
    private ClazzInfoVO clazzInfoVO;
    private TableInfoVO tableInfoVO;
    private TableModule tableModule;
    private FileTypeInfo fileTypeInfo;
    private JavaTemplateVO javaTemplateVO;

    public GeneratorContext(GeneralOrmInfoVO generalOrmInfoVO, FileTypePathEnum fileType, TableInfoVO tableInfoVO) {
        this.generalOrmInfoVO = generalOrmInfoVO;
        this.fileType = fileType;
        this.tableInfoVO = tableInfoVO;
    }

    public GeneratorContext(GeneralOrmInfoVO generalOrmInfoVO, FileTypePathEnum fileType, ClazzInfoVO clazzInfoVO) {
        this.generalOrmInfoVO = generalOrmInfoVO;
        this.fileType = fileType;
        this.clazzInfoVO = clazzInfoVO;
        this.clazzInfoVO.setClazzType(ClazzTypeEnum.CLASS_CLAZZ);
        if (fileType.equals(FileTypePathEnum.DAO) || fileType.equals(FileTypePathEnum.ISERVICE)) {
            this.clazzInfoVO.setClazzType(ClazzTypeEnum.INTERFACE_CLAZZ);
        }
    }

    public GeneratorContext() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public GeneralOrmInfoVO getGeneralOrmInfoVO() {
        return generalOrmInfoVO;
    }

    public void setGeneralOrmInfoVO(GeneralOrmInfoVO generalOrmInfoVO) {
        this.generalOrmInfoVO = generalOrmInfoVO;
    }

    public ClazzInfoVO getClazzInfoVO() {
        return clazzInfoVO;
    }

    public void setClazzInfoVO(ClazzInfoVO clazzInfoVO) {
        this.clazzInfoVO = clazzInfoVO;
    }

    public TableInfoVO getTableInfoVO() {
        return tableInfoVO;
    }

    public void setTableInfoVO(TableInfoVO tableInfoVO) {
        this.tableInfoVO = tableInfoVO;
    }

    public FileTypePathEnum getFileType() {
        return fileType;
    }

    public void setFileType(FileTypePathEnum fileType) {
        this.fileType = fileType;
    }

    public TableModule getTableModule() {
        return tableModule;
    }

    public void setTableModule(TableModule tableModule) {
        this.tableModule = tableModule;
    }

    public FileTypeInfo getFileTypeInfo() {
        return fileTypeInfo;
    }

    public void setFileTypeInfo(FileTypeInfo fileTypeInfo) {
        this.fileTypeInfo = fileTypeInfo;
    }

    public JavaTemplateVO getJavaTemplateVO() {
        return javaTemplateVO;
    }

    public void setJavaTemplateVO(JavaTemplateVO javaTemplateVO) {
        this.javaTemplateVO = javaTemplateVO;
    }


}
