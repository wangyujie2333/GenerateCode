package com.idea.plugin.orm.service;

import com.idea.plugin.document.support.ClazzInfoVO;
import com.idea.plugin.document.support.JavaDocConfig;
import com.idea.plugin.document.support.JavaTypeEnum;
import com.idea.plugin.document.support.MethodInfoVO;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.TableModuleFactory;
import com.idea.plugin.orm.support.enums.ClazzTypeEnum;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.setting.template.JavaTemplateVO;
import com.idea.plugin.sql.support.enums.DataTypeEnum;
import com.idea.plugin.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GeneratorFileStrService {

    public static String packageStr = "package %s;\n\n";
    public static String importStr = "import %s;\n";
    public static String interfaceStr = "public interface %s {\n\n";
    public static String jpainterfaceStr = "public interface %s extends JpaRepository<%s, %s>, JpaSpecificationExecutor<%s> {\n\n";
    public static String calzzStr = "public class %s%s {\n\n";
    public static String calzzImplStr = "public class %s implements %s {\n\n";
    public static String resourceStr = "    @Autowired\n" +
            "    private %s %s;\n";
    public static String methodStr = "    %s %s(%s);\n\n";
    public static String methodImplStr = "    @Override\n" +
            "    public %s %s(%s) {\n" +
            "        %s\n" +
            "    }\n\n";
    public static String methodControllerStr = "    @RequestMapping(method = RequestMethod.POST, value = \"/%s\")\n" +
            "    public %s %s(%s) {\n" +
            "        %s\n" +
            "    }\n\n";

    public static void generationClassClassFile(GeneratorContext context) {
        ProjectGenerator.getFileTypeInfo(context);
        TableModuleFactory.createTableModule(context);
        String classClassStr = null;
        if (context.getFileType().equals(FileTypePathEnum.MAPPER)) {
            if (!JavaTemplateVO.isJpa(context.getJavaTemplateVO())) {
                context.getClazzInfoVO().getClazzInfoDOVO().setClazzName(ProjectGenerator.getImprotStr(context, context.getGeneralOrmInfoVO(), FileTypePathEnum.DO, context.getFileTypeInfo().getFileName()));
                List<String> methodNames = context.getClazzInfoVO().getMethodInfos().stream().map(MethodInfoVO::getMethodName).collect(Collectors.toList());
                classClassStr = GeneratorXmlFileStrService.getXmlFileStr(context, methodNames);
                FileUtils.writeFileDelete(context.getFileTypeInfo().getAbsulotePath(), classClassStr);

                GeneratorContext generatorContextM = new GeneratorContext(context.getGeneralOrmInfoVO(), FileTypePathEnum.MAPPER_MYSQL, context.getTableInfoVO());
                generatorContextM.setClazzInfoVO(context.getClazzInfoVO());
                ProjectGenerator.getFileTypeInfo(generatorContextM);
                TableModuleFactory.createTableModule(generatorContextM);
                classClassStr = GeneratorXmlFileStrService.getXmlBatchMethodStr(generatorContextM, DataTypeEnum.MYSQL.name());
                FileUtils.writeFileDelete(generatorContextM.getFileTypeInfo().getAbsulotePath(), classClassStr);

                GeneratorContext generatorContextO = new GeneratorContext(context.getGeneralOrmInfoVO(), FileTypePathEnum.MAPPER_ORACLE, context.getTableInfoVO());
                generatorContextO.setClazzInfoVO(context.getClazzInfoVO());
                ProjectGenerator.getFileTypeInfo(generatorContextO);
                TableModuleFactory.createTableModule(generatorContextO);
                classClassStr = GeneratorXmlFileStrService.getXmlBatchMethodStr(generatorContextO, DataTypeEnum.ORACLE.name());
                FileUtils.writeFileDelete(generatorContextO.getFileTypeInfo().getAbsulotePath(), classClassStr);
                return;
            }
        } else if (FileTypePathEnum.DO.equals(context.getFileType()) || FileTypePathEnum.VO.equals(context.getFileType())) {
            classClassStr = getClassDOVOClassStr(context);
        } else {
            classClassStr = getClassClassStr(context);
        }
        if (StringUtils.isEmpty(classClassStr)) {
            return;
        }
        FileUtils.writeFileDelete(context.getFileTypeInfo().getAbsulotePath(), classClassStr);
    }

    public static String getClassDOVOClassStr(GeneratorContext context) {
        ClazzInfoVO clazzInfoVO = context.getClazzInfoVO();
        StringBuilder clazzInfoStr = new StringBuilder();
        StringBuilder fieldInfoStr = new StringBuilder();
        StringBuilder methodInfoStr = new StringBuilder();
        List<String> imports = new ArrayList<>();
        Boolean lombok = context.getJavaTemplateVO().getLombok();
        boolean jpa = JavaTemplateVO.isJpa(context.getJavaTemplateVO());
        AtomicBoolean addId = new AtomicBoolean(false);
        clazzInfoVO.getFieldinfos().forEach(fieldInfoVO -> {
            JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(fieldInfoVO.getFieldType());
            javaTypeEnum = javaTypeEnum == null ? JavaTypeEnum.OBJECT_TYPE : javaTypeEnum;
            if (javaTypeEnum.isImport()) {
                imports.add(javaTypeEnum.getCalzz().getName());
            }
            String type = javaTypeEnum.getCalzz().getSimpleName();
            fieldInfoStr.append(String.format(JavaDocConfig.fieldComment, fieldInfoVO.getFieldComent())).append("\n");
            if (!Boolean.TRUE.equals(lombok)) {
                methodInfoStr.append("    " + JavaDocConfig.getGetMethodStr(type, fieldInfoVO.getFieldName())).append("\n\n");
                methodInfoStr.append("    " + JavaDocConfig.getSetMethodStr(type, fieldInfoVO.getFieldName())).append("\n\n");
            }
            if (Boolean.TRUE.equals(jpa) && FileTypePathEnum.DO.equals(context.getFileType())) {
                if (!addId.get()) {
                    addId.set(true);
                    fieldInfoStr.append("    @Id\n");
                }
                fieldInfoStr.append("    @Column(name = \"" + fieldInfoVO.getColumnName() + "\")\n");
            }
            fieldInfoStr.append("    private " + type + " " + fieldInfoVO.getFieldName() + ";\n");
        });
        clazzInfoStr.append(String.format(packageStr, clazzInfoVO.getPackageName()));
        if (Boolean.TRUE.equals(lombok)) {
            imports.add("lombok.Data");
        }
        if (Boolean.TRUE.equals(jpa) && FileTypePathEnum.DO.equals(context.getFileType())) {
            imports.add("javax.persistence.Column");
            imports.add("javax.persistence.Entity");
            imports.add("javax.persistence.Id");
            imports.add("javax.persistence.Table");
        }
        imports.add("java.io.Serializable");
        imports.stream().distinct().forEach(imp -> clazzInfoStr.append(String.format(importStr, imp)));
        clazzInfoStr.append("\n");
        clazzInfoStr.append(JavaDocConfig.getClazzComment(clazzInfoVO.getClazzName()));
        if (Boolean.TRUE.equals(lombok)) {
            clazzInfoStr.append("@Data\n");
        }
        if (Boolean.TRUE.equals(jpa) && FileTypePathEnum.DO.equals(context.getFileType())) {
            clazzInfoStr.append("@Entity\n");
            clazzInfoStr.append("@Table(name = \"" + context.getTableInfoVO().getTableName() + "\")\n");
        }
        clazzInfoStr.append(String.format(calzzStr, clazzInfoVO.getClazzName(), " implements Serializable"));
        clazzInfoStr.append("    private static final long serialVersionUID = 1L;\n\n");
        clazzInfoStr.append(fieldInfoStr);
        if (!Boolean.TRUE.equals(lombok)) {
            clazzInfoStr.append("\n\n");
            clazzInfoStr.append("    " + String.format(JavaDocConfig.constructorMethodStr, clazzInfoVO.getClazzName(), "", ""));
            clazzInfoStr.append("\n\n");
            clazzInfoStr.append(methodInfoStr);
        }
        clazzInfoStr.append("\n}\n");
        return clazzInfoStr.toString();
    }

    public static String getClassClassStr(GeneratorContext context) {
        ClazzInfoVO clazzInfoVO = context.getClazzInfoVO();
        StringBuilder clazzInfoStr = new StringBuilder();
        String classMethodStr = getClassMethodStr(context);
        clazzInfoStr.append(String.format(packageStr, clazzInfoVO.getPackageName()));
        if (CollectionUtils.isNotEmpty(clazzInfoVO.getImportList())) {
            clazzInfoVO.getImportList().forEach(imports -> {
                clazzInfoStr.append(String.format(importStr, imports));
            });
            clazzInfoStr.append("\n");
        }
        clazzInfoStr.append(JavaDocConfig.getClazzComment(clazzInfoVO.getClazzName()));
        if (FileTypePathEnum.SERVICE.equals(context.getFileType())) {
            clazzInfoStr.append("@Service\n");
        } else if (FileTypePathEnum.DAO.equals(context.getFileType())) {
            clazzInfoStr.append("@Repository\n");
        } else if (FileTypePathEnum.CONTROLLER.equals(context.getFileType())) {
            clazzInfoStr.append("@RestController\n");
            clazzInfoStr.append("@RequestMapping(\"/" + context.getFileTypeInfo().getModuleName().toLowerCase() + "/" + FileTypePathEnum.CONTROLLER.getClazzEntiyName(clazzInfoVO.clazzName).toLowerCase() + "\")\n");
        }
        if (FileTypePathEnum.SERVICE.equals(context.getFileType())) {
            clazzInfoStr.append(String.format(calzzImplStr, clazzInfoVO.getClazzName(), clazzInfoVO.getImplClazz()));
        } else if (ClazzTypeEnum.INTERFACE_CLAZZ.equals(clazzInfoVO.getClazzType())) {
            boolean jpa = JavaTemplateVO.isJpa(context.getJavaTemplateVO());
            if (jpa && FileTypePathEnum.DAO.equals(context.getFileType())) {
                String fieldType = clazzInfoVO.getFieldinfos().get(0).getFieldType();
                String doName = FileTypePathEnum.DO.getFileName(context.getTableInfoVO().tableName, context.getJavaTemplateVO());
                clazzInfoStr.append(String.format(jpainterfaceStr, clazzInfoVO.getClazzName(), doName, fieldType, doName));
            } else {
                clazzInfoStr.append(String.format(interfaceStr, clazzInfoVO.getClazzName()));
            }
        } else {
            clazzInfoStr.append(String.format(calzzStr, clazzInfoVO.getClazzName(), ""));
        }
        if (FileTypePathEnum.SERVICE.equals(context.getFileType()) || FileTypePathEnum.CONTROLLER.equals(context.getFileType())) {
            clazzInfoStr.append(String.format(resourceStr, clazzInfoVO.getResourceClazz(), clazzInfoVO.getSimpleName(clazzInfoVO.getResourceClazz())));
        }
        clazzInfoStr.append("\n");
        clazzInfoStr.append(classMethodStr);
        clazzInfoStr.append("\n}\n");
        return clazzInfoStr.toString();
    }

    public static String getClassMethodStr(GeneratorContext context) {
        ClazzInfoVO clazzInfoVO = context.getClazzInfoVO();
        StringBuilder methodInfoStr = new StringBuilder();
        clazzInfoVO.getMethodInfos().forEach(methodInfoVO -> {
            String returnType = clazzInfoVO.getClazzSimpleType(methodInfoVO.getMethodReturn());
            String returnName = clazzInfoVO.getClazzSimpleName(methodInfoVO.getMethodReturn());
            if (FileTypePathEnum.ISERVICE.equals(context.getFileType()) || FileTypePathEnum.DAO.equals(context.getFileType())) {
                methodInfoStr.append(JavaDocConfig.getMethodComment(methodInfoVO));
                methodInfoStr.append(String.format(methodStr, returnType, methodInfoVO.getMethodName(), clazzInfoVO.getMethodParameter(methodInfoVO.getMethodParameter())));
            } else {
                String methodParameter = clazzInfoVO.getMethodParameter(methodInfoVO.getMethodParameter());
                if (FileTypePathEnum.CONTROLLER.equals(context.getFileType())) {
                    methodInfoStr.append(JavaDocConfig.getMethodComment(methodInfoVO));
                    boolean isBody = methodInfoVO.getMethodParameter().values().stream().anyMatch(s ->
                            JavaTypeEnum.codeToEnum(s) != null && (JavaTypeEnum.LIST_TYPE.equals(JavaTypeEnum.codeToEnum(s)) || JavaTypeEnum.MAP_TYPE.equals(JavaTypeEnum.codeToEnum(s))));
                    if (StringUtils.isNotEmpty(methodParameter) && isBody) {
                        methodParameter = "@RequestBody " + methodParameter;
                    }
                }
                String mothodBody = clazzInfoVO.getSimpleName(clazzInfoVO.getResourceClazz()) + "." + methodInfoVO.getMethodName() + "(" + clazzInfoVO.getMethodParam(methodInfoVO.getMethodParameter()) + ");";
                if (FileTypePathEnum.CONTROLLER.equals(context.getFileType())) {
                    String controllerResult = context.getGeneralOrmInfoVO().getControllerResult();
                    returnName= clazzInfoVO.getClazzSimpleName(controllerResult);
                    if (JavaTypeEnum.VOID_TYPE.getBasicName().equals(returnType)) {
                        returnType = controllerResult + "<Object>";
                        mothodBody = mothodBody + "\n        return new " + controllerResult + "();";
                    } else {
                        mothodBody = returnType + " " + returnName + " = " + mothodBody;
                        mothodBody = mothodBody + "\n        return new " + controllerResult + "(" + returnName + ");";
                        returnType = controllerResult + "<" + returnType + ">";
                    }
                    methodInfoStr.append(String.format(methodControllerStr, methodInfoVO.getMethodName(), returnType, methodInfoVO.getMethodName(), methodParameter, mothodBody));
                } else {
                    if (!"void".equals(returnType)) {
                        mothodBody = "return " + mothodBody;
                    }
                    methodInfoStr.append(String.format(methodImplStr, returnType, methodInfoVO.getMethodName(), methodParameter, mothodBody));
                }
            }
        });
        return methodInfoStr.toString();
    }

}
