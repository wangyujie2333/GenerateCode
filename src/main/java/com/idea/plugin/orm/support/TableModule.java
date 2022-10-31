package com.idea.plugin.orm.support;

import com.google.common.base.CaseFormat;
import com.idea.plugin.orm.service.GeneratorFileStrService;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.sql.support.FieldInfoVO;
import com.idea.plugin.sql.support.TableInfoVO;
import com.idea.plugin.sql.support.enums.PrimaryTypeEnum;
import com.idea.plugin.translator.TranslatorFactroy;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TableModule {
    public GeneratorContext context;
    public TableInfoVO tableInfoVO;
    public List<FieldModule> fields = new ArrayList<>();
    public FileTypeInfo fileTypeInfo;

    public TableModule(GeneratorContext context) {
        this.context = context;
        this.tableInfoVO = context.getTableInfoVO();
        for (FieldInfoVO fieldInfo : tableInfoVO.getFieldInfos()) {
            fields.add(new FieldModule(fieldInfo));
        }
        this.fileTypeInfo = context.getFileTypeInfo();
    }

    public List<FieldModule> getFields() {
        return fields;
    }

    public String getPackage() {
        return fileTypeInfo.getPackagePath().replaceAll("/", ".");
    }

    public Set<String> getImports() {
        if (CollectionUtils.isEmpty(context.getClazzInfoVO().getImportList())) {
            return new HashSet<>();
        }
        return new HashSet<>(context.getClazzInfoVO().getImportList());
    }

    public String getMethodsStr() {
        return GeneratorFileStrService.getClassMethodStr(context);
    }

    public String getComment() {
        return TranslatorFactroy.translate(fileTypeInfo.getFileName());
    }

    public String getSimpleName() {
        return fileTypeInfo.getFileName();
    }

    public String getVarName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getSimpleName());
    }

    public FieldInfoVO getIdColumn() {
        return tableInfoVO.getFieldInfos().stream().filter(fieldInfoVO -> fieldInfoVO.primary.equals(PrimaryTypeEnum.PRIMARY))
                .findAny().orElse(tableInfoVO.getFieldInfos().get(0));
    }

    public String getIdColumnName() {
        return getIdColumn().columnName;
    }

    public String getIdName() {
        String idName = getIdColumn().columnName;
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, idName);
    }

    public String getIdType() {
        return getIdColumn().columnType.getJtype().getName();
    }

    public String getIdClass() {
        return getIdColumn().columnType.getJclass().getSimpleName();
    }

    public String getTableName() {
        return tableInfoVO.tableName;
    }

    public String getName() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableInfoVO.tableName);
    }

    public String getAuthor() {
        return context.getGeneralOrmInfoVO().author;
    }

    public String getDate() {
        return context.getGeneralOrmInfoVO().getDate();
    }

    public String getEntityName() {
        return FileTypePathEnum.DO.getFileName(tableInfoVO.tableName, context.getJavaTemplateVO());
    }

    public String getVoName() {
        return FileTypePathEnum.VO.getFileName(tableInfoVO.tableName, context.getJavaTemplateVO());
    }

    public String getMapperName() {
        return FileTypePathEnum.MAPPER.getFileName(tableInfoVO.tableName, context.getJavaTemplateVO());
    }

    public String getIserviceName() {
        return FileTypePathEnum.ISERVICE.getFileName(tableInfoVO.tableName, context.getJavaTemplateVO());
    }

    public String getServiceName() {
        return FileTypePathEnum.SERVICE.getFileName(tableInfoVO.tableName, context.getJavaTemplateVO());
    }

}
