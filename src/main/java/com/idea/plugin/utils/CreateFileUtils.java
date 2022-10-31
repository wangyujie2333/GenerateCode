package com.idea.plugin.utils;

import com.idea.plugin.document.support.ClazzInfoDOVO;
import com.idea.plugin.document.support.ClazzInfoVO;
import com.idea.plugin.document.support.JavaDocConfig;
import com.idea.plugin.document.support.MethodInfoVO;
import com.idea.plugin.orm.service.GeneratorFileStrService;
import com.idea.plugin.orm.service.GeneratorXmlFileStrService;
import com.idea.plugin.orm.service.ProjectGenerator;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.enums.ClazzTypeEnum;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.orm.support.enums.MethodEnum;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.setting.support.TableConfigVO;
import com.idea.plugin.setting.template.JavaTemplateVO;
import com.idea.plugin.sql.AbstractProcedureService;
import com.idea.plugin.sql.support.*;
import com.idea.plugin.sql.support.enums.*;
import com.idea.plugin.translator.TranslatorFactroy;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CreateFileUtils {

    public static void generatorSqlFile(GeneralSqlInfoVO generalSqlInfoVO) {
        AssertUtils.assertIsTrue(StringUtils.isNotEmpty(generalSqlInfoVO.filePath), "文件路径filePath不能为空");
        AssertUtils.assertIsTrue(CollectionUtils.isNotEmpty(generalSqlInfoVO.getTableInfos()), "生成文件类型procedureType不能为空");

        List<ProcedureTypeEnum> procedureTypeEnumList = generalSqlInfoVO.getTableInfos().stream().flatMap(tableInfoVO -> tableInfoVO.getProcedureTypeList().stream()).collect(Collectors.toList());
        List<FileDDLTypeEnum> fileDDLTypeEnums = procedureTypeEnumList.stream().map(ProcedureTypeEnum::getFileType).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        FileDDLTypeEnum fileType = FileDDLTypeEnum.getFirstFileType(fileDDLTypeEnums);
        LocalDateTime localDateTime = LocalDateTime.now();
        String mysqlPath = getFilePath(DataTypeEnum.MYSQL, fileType, generalSqlInfoVO.filePath, generalSqlInfoVO.fileName, localDateTime);
        String oralcePath = getFilePath(DataTypeEnum.ORACLE, fileType, generalSqlInfoVO.filePath, generalSqlInfoVO.fileName, localDateTime);
        addAllProcedure(mysqlPath, oralcePath, generalSqlInfoVO, new TableSqlInfoVO(), ProcedureTypeEnum.INITIAL);
        Class<GeneralSqlInfoVO> generalSqlInfoVOClass = GeneralSqlInfoVO.class;
        Class<TableSqlInfoVO> tableSqlInfoVOClass = TableSqlInfoVO.class;
        generalSqlInfoVO.getTableInfos().stream().filter(tableSqlInfoVO -> tableSqlInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.ADD_COLUMN)).reduce((f, s) -> f)
                .ifPresent(tableSqlInfoVO -> tableSqlInfoVO.setIsMergeAddColumnStart(true));
        generalSqlInfoVO.getTableInfos().stream().filter(tableSqlInfoVO -> tableSqlInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.ADD_INDEX)).reduce((f, s) -> f)
                .ifPresent(tableSqlInfoVO -> tableSqlInfoVO.setIsMergeAddIndexStart(true));
        generalSqlInfoVO.getTableInfos().stream().filter(tableSqlInfoVO -> tableSqlInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.MODIFY_COLUMN)).reduce((f, s) -> f)
                .ifPresent(tableSqlInfoVO -> tableSqlInfoVO.setIsMergeModifyColumnStart(true));
        generalSqlInfoVO.getTableInfos().stream().filter(tableSqlInfoVO -> tableSqlInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.ADD_COLUMN)).reduce((f, s) -> s)
                .ifPresent(tableSqlInfoVO -> tableSqlInfoVO.setIsMergeAddColumnEnd(true));
        generalSqlInfoVO.getTableInfos().stream().filter(tableSqlInfoVO -> tableSqlInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.ADD_INDEX)).reduce((f, s) -> s)
                .ifPresent(tableSqlInfoVO -> tableSqlInfoVO.setIsMergeAddIndexEnd(true));
        generalSqlInfoVO.getTableInfos().stream().filter(tableSqlInfoVO -> tableSqlInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.MODIFY_COLUMN)).reduce((f, s) -> s)
                .ifPresent(tableSqlInfoVO -> tableSqlInfoVO.setIsMergeModifyColumnEnd(true));
        for (TableSqlInfoVO tableInfoVO : generalSqlInfoVO.getTableInfos()) {
            if (tableInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.INSERT_SQL)) {
                DBUtils.setInsertDataInfoVO(generalSqlInfoVO, tableInfoVO);
            }
            for (ProcedureTypeEnum procedureTypeEnum : tableInfoVO.getProcedureTypeList()) {
                if (procedureTypeEnum == null || procedureTypeEnum.getFileType() == null) {
                    continue;
                }
                for (String fieldName : procedureTypeEnum.getMustFieldList()) {
                    Object value;
                    try {
                        Field field = generalSqlInfoVOClass.getField(fieldName);
                        value = field.get(generalSqlInfoVO);
                    } catch (Exception e) {
                        try {
                            Field field = tableSqlInfoVOClass.getField(fieldName);
                            value = field.get(tableInfoVO);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex.getLocalizedMessage(), e);
                        }
                    }
                    if (value == null || StringUtils.isEmpty(value.toString())) {
                        throw new RuntimeException(String.format("生成脚本类型：%s 属性值：%s不能为空", procedureTypeEnum.name(), fieldName));
                    }
                }
                addAllProcedure(mysqlPath, oralcePath, generalSqlInfoVO, tableInfoVO, procedureTypeEnum);
            }
        }
    }

    private static void addAllProcedure(String mysqlPath, String oralcePath, GeneralSqlInfoVO generalSqlInfoVO, TableSqlInfoVO tableSqlInfoVO, ProcedureTypeEnum procedureTypeEnum) {
        generalSqlInfoVO.setProcedureTypeEnum(procedureTypeEnum);
        SqlTemplateModeule sqlTemplateVO = AbstractProcedureService.getSqlTemplateVO(generalSqlInfoVO, tableSqlInfoVO);
        sqlTemplateVO.setFilePath(mysqlPath);
        generalSqlInfoVO.setDataType(DataTypeEnum.MYSQL);
        sqlTemplateVO.setProcedureService(AbstractProcedureService.getAbstractProcedureService(generalSqlInfoVO, DataTypeEnum.MYSQL));
        AbstractProcedureService.getBaseProcedureService(generalSqlInfoVO, DataTypeEnum.MYSQL).addProcedure(mysqlPath, generalSqlInfoVO, tableSqlInfoVO, sqlTemplateVO);

        sqlTemplateVO.setFilePath(oralcePath);
        generalSqlInfoVO.setDataType(DataTypeEnum.ORACLE);
        sqlTemplateVO.setProcedureService(AbstractProcedureService.getAbstractProcedureService(generalSqlInfoVO, DataTypeEnum.ORACLE));

        AbstractProcedureService.getBaseProcedureService(generalSqlInfoVO, DataTypeEnum.ORACLE).addProcedure(oralcePath, generalSqlInfoVO, tableSqlInfoVO, sqlTemplateVO);
    }


    public static String getFilePath(DataTypeEnum dataType, FileDDLTypeEnum fileType, String filePath, String fileName, LocalDateTime localDateTime) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        File file = new File(filePath);
        filePath = filePath + "/" + dataType.getCode();
        if (!file.exists() && !file.isDirectory()) {
            throw new RuntimeException(String.format("文件：%s不存在", filePath));
        } else {
            file = new File(filePath);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }
        }
        return filePath + "/V" + dateFormat.format(localDateTime) + "__" + fileType.getCode() + "." + fileName + "_" + dataType.getCode() + ".sql";
    }


    public static void generatorJavaFile(GeneralOrmInfoVO generalOrmInfoVO, ActionContext context) {
        AssertUtils.assertIsTrue(StringUtils.isNotEmpty(generalOrmInfoVO.modulePath), "文件路径modulePath不能为空");
        AssertUtils.assertIsTrue(CollectionUtils.isNotEmpty(generalOrmInfoVO.getTableInfos()), "生成文件类型procedureType不能为空");

        Class<GeneralOrmInfoVO> generalOrmInfoVOClass = GeneralOrmInfoVO.class;
        Class<TableOrmInfoVO> tableOrmInfoVOClass = TableOrmInfoVO.class;
        for (TableOrmInfoVO tableInfoVO : generalOrmInfoVO.getTableInfos()) {
            TableConfigVO config = ToolSettings.getTableConfig();
            if (StringUtils.isNotEmpty(tableInfoVO.getInterfaceClazz())) {
                generalFromFile(generalOrmInfoVO, context, tableInfoVO);
                if (tableInfoVO.getProcedureTypeList().contains(ProcedureTypeEnum.DO)) {
                    tableInfoVO.setProcedureType(ProcedureTypeEnum.DO.name());
                    generalFromDB(generalOrmInfoVO, generalOrmInfoVOClass, tableOrmInfoVOClass, tableInfoVO, config);
                }
            } else {
                generalFromDB(generalOrmInfoVO, generalOrmInfoVOClass, tableOrmInfoVOClass, tableInfoVO, config);
            }
        }
    }

    public static void generalFromFile(GeneralOrmInfoVO generalOrmInfoVO, ActionContext context, TableOrmInfoVO tableInfoVO) {
        for (String interfaceClazz : tableInfoVO.getInterfaceClazz().split(";")) {
            String[] interfaceClazzArr = interfaceClazz.split("#");
            List<String> methodNames = new ArrayList<>();
            if (interfaceClazzArr.length == 1) {
                interfaceClazz = interfaceClazzArr[0].trim();
            }
            if (interfaceClazzArr.length == 2) {
                interfaceClazz = interfaceClazzArr[0].trim();
                methodNames = Arrays.stream(interfaceClazzArr[1].trim().split(",")).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            }
            List<ClazzInfoVO> clazzInfoVOS = JavaDocConfig.getClazzInfoVOS(context.getProject(), interfaceClazz);
            if (CollectionUtils.isEmpty(clazzInfoVOS)) {
                continue;
            }
            JavaDocConfig.setClazzInfoDOVO(context.getProject(), clazzInfoVOS);
            ClazzInfoVO clazzInfoVO = clazzInfoVOS.get(0);
            if (CollectionUtils.isEmpty(methodNames) && CollectionUtils.isNotEmpty(clazzInfoVO.getMethodInfos())) {
                methodNames = clazzInfoVO.getMethodInfos().stream().map(MethodInfoVO::getMethodName).collect(Collectors.toList());
            }
            for (ProcedureTypeEnum procedureTypeEnum : tableInfoVO.getProcedureTypeList()) {
                if (procedureTypeEnum == null || procedureTypeEnum.getFileCreateType() == null) {
                    continue;
                }
                for (FileTypePathEnum fileTypePathEnum : procedureTypeEnum.getFileCreateType().getFileTypePathList()) {
                    if (fileTypePathEnum.equals(FileTypePathEnum.DO)
                            || fileTypePathEnum.equals(FileTypePathEnum.VO)
                            || fileTypePathEnum.equals(FileTypePathEnum.MAPPER_MYSQL)
                            || fileTypePathEnum.equals(FileTypePathEnum.MAPPER_ORACLE)) {
                        continue;
                    }
                    GeneratorContext generatorContext = new GeneratorContext(generalOrmInfoVO, fileTypePathEnum, clazzInfoVO);
                    generatorContext.setProject(context.getProject());
                    ProjectGenerator.getFileTypeInfo(generatorContext);
                    if (CollectionUtils.isNotEmpty(methodNames)) {
                        List<String> finalMethodNames = methodNames;
                        clazzInfoVO.setMethodInfos(clazzInfoVO.getMethodInfos().stream().filter(methodInfoVO -> finalMethodNames.contains(methodInfoVO.getMethodName())).collect(Collectors.toList()));
                    }
                    File file = new File(generatorContext.getFileTypeInfo().getAbsulotePath());
                    if (file.isFile()) {
                        String fileName = generatorContext.getFileTypeInfo().getFileName();
                        if (fileTypePathEnum.equals(FileTypePathEnum.MAPPER)) {
                            String fileStr = FileUtils.readFileStr(file.getPath());
                            Pattern compile = Pattern.compile("id([ |=]+)\"(\\w+)\"");
                            Matcher matcher = compile.matcher(fileStr);
                            while (matcher.find()) {
                                methodNames.removeIf(methodName -> methodName.equals(matcher.group(2)));
                            }
                            String classMethodStr = GeneratorXmlFileStrService.getXmlMethodStr(generatorContext, methodNames);
                            if (StringUtils.isEmpty(classMethodStr)) {
                                continue;
                            }
                            String fileTypeName = generatorContext.getFileTypeInfo().getFileName() + "." + fileTypePathEnum.getFileType().getType();
                            PsiFile[] psiFiles = FilenameIndex.getFilesByName(context.getProject(), fileTypeName, GlobalSearchScope.allScope(context.getProject()));
                            if (psiFiles.length == 0) {
                                continue;
                            }
                            String targetFileText = psiFiles[0].getText();
                            targetFileText = targetFileText.substring(0, targetFileText.lastIndexOf("</mapper>")) + classMethodStr + targetFileText.substring(targetFileText.lastIndexOf("</mapper>"));
                            FileUtils.writeFileDelete(generatorContext.getFileTypeInfo().getAbsulotePath(), String.join("\n", targetFileText));

                        } else {
                            List<ClazzInfoVO> targetClazzInfoVOS = JavaDocConfig.getClazzInfoVOS(context.getProject(), fileName);
                            if (CollectionUtils.isEmpty(targetClazzInfoVOS)) {
                                continue;
                            }
                            ClazzInfoVO targetClazzInfoVO = targetClazzInfoVOS.get(0);
                            if (CollectionUtils.isNotEmpty(targetClazzInfoVO.getMethodInfos())) {
                                List<String> methodInfoList = targetClazzInfoVO.getMethodInfos().stream().map(methodInfoVO -> {
                                    if (MapUtils.isNotEmpty(methodInfoVO.getMethodParameter())) {
                                        return methodInfoVO.getMethodName() + methodInfoVO.getMethodParameter().size();
                                    }
                                    return methodInfoVO.getMethodName();
                                }).collect(Collectors.toList());
                                List<MethodInfoVO> methodInfoVOS = clazzInfoVO.getMethodInfos().stream().filter(methodInfoVO -> {
                                    if (methodInfoVO.getPsiMethod().hasModifierProperty(PsiModifier.STATIC)
                                            || methodInfoVO.getPsiMethod().hasModifierProperty(PsiModifier.FINAL)
                                            || methodInfoVO.getPsiMethod().hasModifierProperty(PsiModifier.PRIVATE)) {
                                        return false;
                                    }
                                    String methodName = methodInfoVO.getMethodName();
                                    if (MapUtils.isNotEmpty(methodInfoVO.getMethodParameter())) {
                                        methodName = methodName + methodInfoVO.getMethodParameter().size();
                                    }
                                    return !methodInfoList.contains(methodName);
                                }).collect(Collectors.toList());
                                targetClazzInfoVO.setMethodInfos(methodInfoVOS);
                                GeneratorContext targetGeneratorContext = new GeneratorContext(generalOrmInfoVO, fileTypePathEnum, targetClazzInfoVO);
                                ProjectGenerator.getFileTypeInfo(targetGeneratorContext);
                                String classMethodStr = GeneratorFileStrService.getClassMethodStr(targetGeneratorContext);
                                if (StringUtils.isEmpty(classMethodStr)) {
                                    continue;
                                }
                                String fileTypeName = generatorContext.getFileTypeInfo().getFileName() + "." + fileTypePathEnum.getFileType().getType();
                                PsiFile[] psiFiles = FilenameIndex.getFilesByName(context.getProject(), fileTypeName, GlobalSearchScope.allScope(context.getProject()));
                                if (psiFiles.length == 0) {
                                    continue;
                                }
                                String targetFileText = psiFiles[0].getText();
                                targetFileText = targetFileText.substring(0, targetFileText.lastIndexOf("}")) + classMethodStr + targetFileText.substring(targetFileText.lastIndexOf("}"));
                                FileUtils.writeFileDelete(generatorContext.getFileTypeInfo().getAbsulotePath(), String.join("\n", targetFileText));
                            }
                        }

                    } else {
                        String classClassStr;
                        if (fileTypePathEnum.equals(FileTypePathEnum.MAPPER)) {
                            classClassStr = GeneratorXmlFileStrService.getXmlFileStr(generatorContext, methodNames);
                        } else {
                            classClassStr = GeneratorFileStrService.getClassClassStr(generatorContext);
                        }
                        FileUtils.writeFileDelete(generatorContext.getFileTypeInfo().getAbsulotePath(), classClassStr);
                    }
                }
            }
        }
    }

    public static void generalFromDB(GeneralOrmInfoVO generalOrmInfoVO, Class<GeneralOrmInfoVO> generalOrmInfoVOClass, Class<TableOrmInfoVO> tableOrmInfoVOClass, TableOrmInfoVO tableInfoVO, TableConfigVO config) {
        TableInfoVO tableInfoVOFromDB = DBUtils.getTableInfoVOFromDB(generalOrmInfoVO, tableInfoVO, config);
        tableInfoVO.tableComment = tableInfoVOFromDB.tableComment;
        tableInfoVO.fieldInfos = tableInfoVOFromDB.getFieldInfos();
        generalJavaFromDb(generalOrmInfoVO, generalOrmInfoVOClass, tableOrmInfoVOClass, tableInfoVO);
    }

    public static void generalFromDbTable(GeneralOrmInfoVO generalOrmInfoVO, Class<GeneralOrmInfoVO> generalOrmInfoVOClass, Class<TableOrmInfoVO> tableOrmInfoVOClass, DbTable dbTable) {
        TableInfoVO tableInfoVO = getTableInfoVO(dbTable);
        tableInfoVO.setProcedureType(generalOrmInfoVO.getTableInfos().get(0).getProcedureType());
        generalJavaFromDb(generalOrmInfoVO, generalOrmInfoVOClass, tableOrmInfoVOClass, tableInfoVO);
    }

    public static TableInfoVO getTableInfoVO(DbTable dbTable) {
        TableInfoVO tableInfoVO = new TableInfoVO();
        tableInfoVO.tableComment = StringUtils.isEmpty(dbTable.getComment()) ? TranslatorFactroy.translate(dbTable.getName()) : dbTable.getComment();
        tableInfoVO.tableName = dbTable.getName();
        DasUtil.getColumns(dbTable)
                .forEach(it -> {
                    FieldInfoVO fieldInfoVO = new FieldInfoVO();
                    fieldInfoVO.columnName = it.getName();
                    JDBCType jdbcType = DBUtils.convertToJdbcType(it.getDataType().typeName, dbTable.getDataSource().getDatabaseVersion().name);
                    if (jdbcType == null) {
                        jdbcType = JDBCType.VARCHAR;
                    }
                    fieldInfoVO.columnType = FieldTypeEnum.getFieldTypeBySqlType(jdbcType.getVendorTypeNumber());
                    String comment = it.getComment();
                    if (StringUtils.isEmpty(comment)) {
                        comment = TranslatorFactroy.translate(it.getName());
                    }
                    fieldInfoVO.comment = comment;
                    if (DasUtil.isPrimary(it)) {
                        fieldInfoVO.primary = PrimaryTypeEnum.PRIMARY;
                    }
                    if (it.isNotNull()) {
                        fieldInfoVO.nullType = NullTypeEnum.NOT_NULL;
                    }
                    if (FieldTypeEnum.VARCHAR.equals(fieldInfoVO.columnType)) {
                        fieldInfoVO.columnTypeArgs = String.valueOf(it.getDataType().size);
                    }
                    if (FieldTypeEnum.NUMBER.equals(fieldInfoVO.columnType)) {
                        fieldInfoVO.columnTypeArgs = it.getDataType().size + "," + it.getDataType().scale;
                    }
                    tableInfoVO.addFieldInfos(fieldInfoVO);
                });
        return tableInfoVO;
    }

    private static void generalJavaFromDb(GeneralOrmInfoVO generalOrmInfoVO, Class<GeneralOrmInfoVO> generalOrmInfoVOClass, Class<TableOrmInfoVO> tableOrmInfoVOClass, TableInfoVO tableInfoVO) {
        if (CollectionUtils.isEmpty(generalOrmInfoVO.getMethods())) {
            generalOrmInfoVO.setMethods(MethodEnum.getDefaultMthods());
        }
        ReportConfigVO reportConfig = ToolSettings.getReportConfig();
        AtomicBoolean batch = new AtomicBoolean(false);
        ClazzInfoVO clazzInfoVO = new ClazzInfoVO();
        clazzInfoVO.setFieldinfos(tableInfoVO.getFieldInfos().stream().map(com.idea.plugin.document.support.FieldInfoVO::new).collect(Collectors.toList()));
        ClazzInfoDOVO clazzInfoDOVO = new ClazzInfoDOVO();
        clazzInfoDOVO.setTableName(tableInfoVO.tableName);
        clazzInfoDOVO.setClazzName(FileTypePathEnum.DO.getFileName(tableInfoVO.tableName, reportConfig.javaTemplateVO));
        clazzInfoDOVO.setFieldinfos(tableInfoVO.getFieldInfos().stream().map(com.idea.plugin.document.support.FieldInfoVO::new).collect(Collectors.toList()));
        List<MethodInfoVO> methodInfos = generalOrmInfoVO.getMethods().stream().map(methodName -> {
            if (methodName.endsWith("Batch")) {
                batch.set(true);
            }
            MethodEnum methodEnum = MethodEnum.codeToEnum(methodName);
            String ret = methodEnum.getRet(tableInfoVO.tableName, reportConfig.getJavaTemplateVO());
            if (methodName.contains("insert") || methodName.contains("update") || methodName.contains("Batch")) {
                ret = "int";
            }
            String param = methodEnum.getParam(tableInfoVO.tableName, reportConfig.getJavaTemplateVO(), generalOrmInfoVO);
            MethodInfoVO methodInfoVO = new MethodInfoVO();
            methodInfoVO.setMethodName(methodName);
            methodInfoVO.setMethodReturn(ret);
            Map<String, String> methodParameter = new HashMap();
            methodParameter.put(clazzInfoVO.getClazzSimpleName(param), param);
            methodInfoVO.setMethodParameter(methodParameter);
            methodInfoVO.getMethodParameterInfos().add(param + " " + clazzInfoVO.getClazzSimpleName(param));
            return methodInfoVO;
        }).collect(Collectors.toList());
        clazzInfoVO.setMethodInfos(methodInfos);
        clazzInfoVO.addClazzImports();
        for (ProcedureTypeEnum procedureTypeEnum : tableInfoVO.getProcedureTypeList()) {
            if (procedureTypeEnum == null || procedureTypeEnum.getFileCreateType() == null) {
                continue;
            }
            for (String fieldName : procedureTypeEnum.getMustFieldList()) {
                Object value;
                try {
                    Field field = generalOrmInfoVOClass.getField(fieldName);
                    value = field.get(generalOrmInfoVO);
                } catch (Exception e) {
                    try {
                        Field field = tableOrmInfoVOClass.getField(fieldName);
                        value = field.get(tableInfoVO);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex.getLocalizedMessage(), e);
                    }
                }
                if (value == null || StringUtils.isEmpty(value.toString())) {
                    throw new RuntimeException(String.format("生成脚本类型：%s 属性值：%s不能为空", procedureTypeEnum.name(), fieldName));
                }
            }
            ClazzInfoVO copyClazzInfoVO = JsonUtil.fromJson(JsonUtil.toJson(clazzInfoVO), ClazzInfoVO.class);
            for (FileTypePathEnum fileTypePathEnum : procedureTypeEnum.getFileCreateType().getFileTypePathList()) {
                GeneratorContext generatorContext = new GeneratorContext(generalOrmInfoVO, fileTypePathEnum, tableInfoVO);
                generatorContext.setClazzInfoVO(copyClazzInfoVO);
                copyClazzInfoVO.setClazzType(ClazzTypeEnum.CLASS_CLAZZ);
                if (fileTypePathEnum.equals(FileTypePathEnum.DAO) || fileTypePathEnum.equals(FileTypePathEnum.ISERVICE)) {
                    copyClazzInfoVO.setClazzType(ClazzTypeEnum.INTERFACE_CLAZZ);
                }
                copyClazzInfoVO.setClazzInfoDOVO(clazzInfoDOVO);
                if (reportConfig.getJavaTemplateVO() != null) {
                    generatorContext.setJavaTemplateVO(reportConfig.getJavaTemplateVO());
                    GeneratorFileStrService.generationClassClassFile(generatorContext);
                } else {
                    ProjectGenerator.generationFile(generatorContext);
                }
            }
            if ((!JavaTemplateVO.isJpa(reportConfig.getJavaTemplateVO()))
                    && ProcedureTypeEnum.DAO.equals(procedureTypeEnum)
                    && batch.get()) {
                GeneratorContext generatorContextM = new GeneratorContext(generalOrmInfoVO, FileTypePathEnum.MAPPER_MYSQL, tableInfoVO);
                generatorContextM.setClazzInfoVO(copyClazzInfoVO);
                ProjectGenerator.generationFile(generatorContextM);
                GeneratorContext generatorContextO = new GeneratorContext(generalOrmInfoVO, FileTypePathEnum.MAPPER_ORACLE, tableInfoVO);
                generatorContextO.setClazzInfoVO(copyClazzInfoVO);
                ProjectGenerator.generationFile(generatorContextO);
            }
        }
    }

}
