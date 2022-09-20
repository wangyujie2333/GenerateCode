package com.idea.plugin.orm.service;

import com.idea.plugin.document.support.ClazzInfoVO;
import com.idea.plugin.document.support.JavaDocConfig;
import com.idea.plugin.document.support.JavaTypeEnum;
import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.enums.ClazzTypeEnum;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class GeneratorFileStrService {

    public static String packageStr = "package %s;\n\n";
    public static String importStr = "import %s;\n";
    public static String interfaceStr = "\npublic interface %s {\n\n";
    public static String calzzStr = "public class %s {\n\n";
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
        } else if (FileTypePathEnum.CONTROLLER.equals(context.getFileType())) {
            clazzInfoStr.append("@RestController\n");
            clazzInfoStr.append("@RequestMapping(\"/" + context.getFileTypeInfo().getModuleName().toLowerCase() + "/" + FileTypePathEnum.CONTROLLER.getClazzEntiyName(clazzInfoVO.clazzName).toLowerCase() + "\")\n");
        }
        if (FileTypePathEnum.SERVICE.equals(context.getFileType())) {
            clazzInfoStr.append(String.format(calzzImplStr, clazzInfoVO.getClazzName(), clazzInfoVO.getImplClazz()));
        } else if (ClazzTypeEnum.INTERFACE_CLAZZ.equals(clazzInfoVO.getClazzType())) {
            clazzInfoStr.append(String.format(interfaceStr, clazzInfoVO.getClazzName()));
        } else {
            clazzInfoStr.append(String.format(calzzStr, clazzInfoVO.getClazzName()));
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
