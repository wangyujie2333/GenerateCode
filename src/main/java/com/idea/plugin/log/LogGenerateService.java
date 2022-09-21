package com.idea.plugin.log;

import com.idea.plugin.document.support.JavaTypeEnum;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.setting.template.LogTemplateVO;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class LogGenerateService {
    private static final String logger = "private static final Logger logger = LoggerFactory.getLogger(%s.class);\n";
    private static final String logCommon = "logger.info(\"method {} variable {} is -> {}\", %s, %s, %s);";
    private static final String logEnter = "logger.debug(\"enter {} method\", %s);";
    private static final String logLeave = "logger.debug(\"leave {} method\", %s);";
    private static final String logError = "logger.error(\"{} error msg is: {}\", %s, %s.getMessage(), %s);";

    public void doGenerate(ActionContext context) {
        try {
            PsiElement psiElement = context.getPsiElement();
            if (context.getEditor() == null && context.getVirtualFiles().length > 0) {
                for (VirtualFile virtualFile : context.getVirtualFiles()) {
                    String fileName = virtualFile.getName().substring(0, virtualFile.getName().lastIndexOf("."));
                    PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(fileName, GlobalSearchScope.projectScope(context.getProject()));
                    if (psiClasses.length == 0) {
                        return;
                    }
                    writerClazzLog(context, psiClasses[0]);
                }
            }
            if (psiElement == null || context.getEditor() == null || context.getVirtualFiles().length == 0) {
                return;
            }
            VirtualFile virtualFile = context.getVirtualFiles()[0];
            String fileName = virtualFile.getName().substring(0, virtualFile.getName().lastIndexOf("."));
            PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(fileName, GlobalSearchScope.projectScope(context.getProject()));
            if (psiClasses.length == 0) {
                return;
            }
            List<Caret> allCarets = context.getEditor().getCaretModel().getAllCarets();
            PsiClass psiClass = psiClasses[0];
            if (allCarets.size() == 1) {
                if (psiElement.getNode() != null && psiElement instanceof PsiClass) {
                    PsiClass[] innerClasses = psiClass.getInnerClasses();
                    Optional<String> isInnerClazzOptional = Arrays.stream(innerClasses).map(NavigationItem::getName).filter(Objects::nonNull).filter(s -> s.equals(((PsiClass) psiElement).getName())).findAny();
                    Caret caret = allCarets.get(0);
                    int offset = caret.getOffset();
                    int textsOffset = psiElement.getTextOffset();
                    int texteOffset = psiElement.getTextOffset() + ((PsiClass) psiElement).getName().length();
                    if ((offset <= texteOffset && offset >= textsOffset) || isInnerClazzOptional.isPresent()) {
                        writerClazzLog(context, (PsiClass) psiElement);
                    } else {
                        writeCommonLog(context, psiClass, allCarets.get(0));
                    }
                } else if (psiElement.getNode() != null && psiElement instanceof PsiMethod) {
                    Caret caret = allCarets.get(0);
                    int offset = caret.getOffset();
                    int textsOffset = psiElement.getTextOffset();
                    int texteOffset = psiElement.getTextOffset() + ((PsiMethod) psiElement).getName().length();
                    if (offset <= texteOffset && offset >= textsOffset) {
                        writeMethodLog(context, (PsiMethod) psiElement, (PsiClass) psiElement.getParent());
                    } else {
                        writeCommonLog(context, psiClass, allCarets.get(0));
                    }
                } else {
                    writeCommonLog(context, psiClass, allCarets.get(0));
                }
            } else {
                for (Caret caret : allCarets) {
                    writeCommonLog(context, psiClass, caret);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    private void writerClazzLog(ActionContext context, PsiClass psiClass) {
        Arrays.stream(psiClass.getMethods()).filter(psiMethod -> psiMethod.isWritable() && !psiMethod.isConstructor()
                        && psiMethod.hasModifierProperty(PsiModifier.PUBLIC)
                        && !psiMethod.hasModifierProperty(PsiModifier.STATIC))
                .forEach(psiMethod -> writeMethodLog(context, psiMethod, psiClass));
    }

    private void writeCommonLog(ActionContext context, PsiClass psiClass, Caret caret) {
        int offset = caret.getOffset();
        PsiElement psiElement = context.getPsiFile().findElementAt(offset);
        if (null == psiElement || psiElement.getText().equals(psiClass.getName()) || psiElement instanceof PsiWhiteSpace) {
            psiElement = context.getPsiFile().findElementAt(offset - 1);
        }
        if (null == psiElement || psiElement.getText().equals(psiClass.getName()) || psiElement instanceof PsiWhiteSpace) {
            psiElement = context.getPsiFile().findElementAt(offset + 1);
        }
        if (psiElement == null) {
            return;
        }
        PsiElement finalPsiElement = psiElement;
        WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
            final PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(context.getProject());
            PsiElement pPsiElement = finalPsiElement;
            while (!(pPsiElement instanceof PsiClass) && pPsiElement != null) {
                PsiMethod psiMethod = null;
                PsiExpressionStatement psiExpressionStatement = null;
                PsiDeclarationStatement psiDeclarationStatement = null;
                PsiTryStatement psiTryStatement = null;
                PsiStatement psiStatement = null;
                PsiElement mPsiElement = pPsiElement;
                while (!(mPsiElement instanceof PsiClass) && mPsiElement != null) {
                    if (mPsiElement instanceof PsiMethod) {
                        psiMethod = (PsiMethod) mPsiElement;
                        break;
                    }
                    if (psiExpressionStatement == null && mPsiElement instanceof PsiExpressionStatement) {
                        psiExpressionStatement = (PsiExpressionStatement) mPsiElement;
                    }
                    if (psiDeclarationStatement == null && mPsiElement instanceof PsiDeclarationStatement) {
                        psiDeclarationStatement = (PsiDeclarationStatement) mPsiElement;
                    }
                    if (psiTryStatement == null && mPsiElement instanceof PsiTryStatement) {
                        psiTryStatement = (PsiTryStatement) mPsiElement;
                    }
                    if (psiStatement == null && mPsiElement instanceof PsiStatement) {
                        psiStatement = (PsiStatement) mPsiElement;
                    }
                    mPsiElement = mPsiElement.getParent();
                }
                if (psiMethod == null || psiMethod.getBody() == null) {
                    break;
                }
                if (finalPsiElement instanceof PsiIdentifier) {
                    PsiElement parent = finalPsiElement.getParent();
                    if (parent instanceof PsiExpression) {
                        PsiType psiType = ((PsiExpression) parent).getType();
                        PsiElement logCommonCode = psiElementFactory.createStatementFromText(getLogCommon(psiMethod, psiType, pPsiElement.getText()), finalPsiElement.getParent());
                        if (psiExpressionStatement != null) {
                            psiExpressionStatement.getParent().addAfter(logCommonCode, psiExpressionStatement);
                        } else if (psiDeclarationStatement != null) {
                            psiDeclarationStatement.getParent().addAfter(logCommonCode, psiDeclarationStatement);
                        } else if (psiStatement != null) {
                            psiStatement.getParent().addAfter(logCommonCode, psiStatement);
                        }
                        break;
                    } else if (parent instanceof PsiLocalVariable || parent instanceof PsiCatchSection) {
                        pPsiElement = parent;
                    }
                }
                if (pPsiElement instanceof PsiLocalVariable) {
                    int offsetAdd = pPsiElement.getTextOffset() + 1;
                    PsiElement elementAt = null;
                    int lineNumberCurrent = context.getDocument().getLineNumber(offset);
                    int lineNumberAdd = context.getDocument().getLineNumber(offsetAdd);
                    while (lineNumberCurrent == lineNumberAdd) {
                        lineNumberAdd = context.getDocument().getLineNumber(offsetAdd);
                        PsiElement element = context.getPsiFile().findElementAt(++offsetAdd);
                        if (element instanceof PsiIdentifier) {
                            elementAt = element;
                            break;
                        }
                    }
                    if (elementAt != null) {
                        PsiType psiType = ((PsiLocalVariable) pPsiElement).getType();
                        PsiElement logCommonCode = psiElementFactory.createStatementFromText(getLogCommon(psiMethod, psiType, elementAt.getText()), finalPsiElement.getParent());
                        if (psiExpressionStatement != null) {
                            psiExpressionStatement.getParent().addAfter(logCommonCode, psiExpressionStatement);
                        } else if (psiDeclarationStatement != null) {
                            psiDeclarationStatement.getParent().addAfter(logCommonCode, psiDeclarationStatement);
                        } else if (psiStatement != null) {
                            psiStatement.getParent().addAfter(logCommonCode, psiStatement);
                        }
                    }
                    break;
                }
                if (pPsiElement instanceof PsiCatchSection) {
                    String ex = "e";
                    for (PsiElement child1 : pPsiElement.getChildren()) {
                        if (child1 instanceof PsiParameter) {
                            ex = child1.getLastChild().getText();
                        }
                        if (child1 instanceof PsiCodeBlock) {
                            PsiElement logErrorCode = psiElementFactory.createStatementFromText(getLogError(psiMethod, ex), psiMethod.getContext());
                            if (psiTryStatement != null) {
                                psiTryStatement.addAfter(logErrorCode, child1.getFirstChild());
                            } else if (psiStatement != null) {
                                psiStatement.getParent().addAfter(logErrorCode, child1.getFirstChild());
                            }
                        }
                    }
                    break;
                }
                pPsiElement = pPsiElement.getParent();
            }
        });
    }

    private void writeMethodLog(ActionContext context, PsiMethod psiMethod, PsiClass psiClass) {
        PsiCodeBlock body = psiMethod.getBody();
        if (body == null) {
            return;
        }
        WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
            final PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(context.getProject());
            if (Arrays.stream(psiClass.getFields()).noneMatch(psiField -> psiField.getName().equals("logger"))) {
                PsiField psiField = psiElementFactory.createFieldFromText(String.format(logger, psiClass.getName()), null);
                PsiField firstField = null;
                if (psiClass.getFields().length > 0) {
                    firstField = psiClass.getFields()[0];
                }
                psiClass.addBefore(psiField, firstField);
                try {
                    PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) psiClass.getParent();
                    PsiClass loggerClass = JavaPsiFacade.getInstance(context.getProject()).findClass("org.slf4j.Logger", GlobalSearchScope.allScope(context.getProject()));
                    PsiClass loggerFactoryClass = JavaPsiFacade.getInstance(context.getProject()).findClass("org.slf4j.LoggerFactory", GlobalSearchScope.allScope(context.getProject()));
                    PsiImportStatement loggerImport = psiElementFactory.createImportStatement(loggerClass);
                    PsiImportStatement loggerFactoryImport = psiElementFactory.createImportStatement(loggerFactoryClass);
                    psiJavaFile.getImportList().add(loggerImport);
                    psiJavaFile.getImportList().add(loggerFactoryImport);
                } catch (Exception ignore) {
                }
            }
            PsiElement firstChild = body.getFirstChild();
            PsiElement logEnterCode = psiElementFactory.createStatementFromText(getLogEnter(psiMethod, firstChild), psiMethod.getContext());
            body.addAfter(logEnterCode, firstChild);
            writeLeaveLog(psiMethod, body, psiElementFactory, body.getChildren());
            if (PsiType.VOID.equals(psiMethod.getReturnType())) {
                PsiElement lastChild = body.getLastChild();
                PsiElement logLeaveCode = psiElementFactory.createStatementFromText(getLogLeave(psiMethod, lastChild), psiMethod.getContext());
                body.addBefore(logLeaveCode, lastChild);
            }
        });
    }

    private void writeLeaveLog(PsiMethod psiMethod, PsiElement body, PsiElementFactory psiElementFactory, PsiElement[] children) {
        for (PsiElement child : children) {
            if (child instanceof PsiReturnStatement) {
                PsiElement logLeaveCode = psiElementFactory.createStatementFromText(getLogLeave(psiMethod, child), psiMethod.getContext());
                body.addBefore(logLeaveCode, child);
            } else if (child instanceof PsiCatchSection) {
                String ex = "e";
                for (PsiElement child1 : child.getChildren()) {
                    if (child1 instanceof PsiParameter) {
                        ex = child1.getLastChild().getText();
                    }
                    if (child1 instanceof PsiCodeBlock) {
                        PsiElement logLeaveCode = psiElementFactory.createStatementFromText(getLogLeave(psiMethod, child), psiMethod.getContext());
                        body.addAfter(logLeaveCode, child1.getFirstChild());
                        PsiElement logErrorCode = psiElementFactory.createStatementFromText(getLogError(psiMethod, ex), psiMethod.getContext());
                        body.addAfter(logErrorCode, child1.getFirstChild());
                    }
                }
            }
            if (child.getChildren().length > 0) {
                writeLeaveLog(psiMethod, child, psiElementFactory, child.getChildren());
            }
        }
    }


    private String getLog(PsiMethod psiMethod, PsiElement child, LogTypeEnum logTypeEnum) {
        ReportConfigVO reportConfig = ToolSettings.getReportConfig();
        if (reportConfig != null && reportConfig.getLogTemplate() != null) {
            LogTemplateVO logTemplate = reportConfig.getLogTemplate();
            String logStr = null;
            String logMsgStr = null;
            if (LogTypeEnum.LOG_ENTER.equals(logTypeEnum)) {
                logStr = logTemplate.getLogEnter();
                logMsgStr = logTemplate.getLogEnterMsg();
            } else if (LogTypeEnum.LOG_LEAVE.equals(logTypeEnum)) {
                logStr = logTemplate.getLogLeave();
                logMsgStr = logTemplate.getLogLeaverMsg();
            }
            if (logMsgStr != null) {
                List<String> logMsg = Arrays.stream(logMsgStr.split("[,，]")).map(String::trim).collect(Collectors.toList());
                List<String> argsList = new ArrayList<>();
                for (String msg : logMsg) {
                    if (msg.contains("methodName")) {
                        argsList.add("\"" + psiMethod.getName() + "\"");
                    } else if (msg.contains("params")) {
                        PsiParameterList parameterList = psiMethod.getParameterList();
                        PsiParameter[] parameters = parameterList.getParameters();
                        if (parameters.length > 0) {
                            List<String> param = new ArrayList<>();
                            for (PsiParameter parameter : parameters) {
                                if (parameter != null) {
                                    String name = parameter.getName();
                                    PsiType type = parameter.getType();
                                    if (StringUtils.isEmpty(logTemplate.getSerial())) {
                                        param.add("\"" + name + ":\" + " + name);
                                    } else {
                                        JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(type.getCanonicalText());
                                        if (javaTypeEnum == null || JavaTypeEnum.MAP_TYPE.equals(javaTypeEnum) || JavaTypeEnum.LIST_TYPE.equals(javaTypeEnum)) {
                                            param.add("\"" + name + ":\" + " + String.format(logTemplate.getSerial(), name));
                                        } else {
                                            param.add("\"" + name + ":\" + " + name);
                                        }
                                    }
                                }
                            }
                            argsList.add(String.join(" + ", param));
                        }
                    } else if (msg.contains("return")) {
                        PsiType returnType = psiMethod.getReturnType();
                        if (!PsiType.VOID.equals(returnType)) {
                            for (PsiElement psiElement : child.getChildren()) {
                                if (psiElement instanceof PsiExpression) {
                                    String returnText = psiElement.getText();
                                    if (StringUtils.isEmpty(logTemplate.getSerial()) || returnType instanceof PsiPrimitiveType) {
                                        argsList.add(returnText);
                                    } else if (returnType instanceof PsiArrayType || returnType instanceof PsiClassType) {
                                        argsList.add(String.format(logTemplate.getSerial(), returnText));
                                    } else {
                                        argsList.add(returnText);
                                    }
                                }
                            }
                        }
                    } else {
                        argsList.add("\"" + msg + "\"");
                    }
                }
                String format = argsList.stream().map(s -> "%s").collect(Collectors.joining(", "));
                logStr = String.format(logStr, format);
                return String.format(logStr, argsList.toArray());
            }
        }
        return String.format(logTypeEnum.getCode(), "\"" + psiMethod.getName() + "\"");
    }

    private String getLogEnter(PsiMethod psiMethod, PsiElement child) {
        return getLog(psiMethod, child, LogTypeEnum.LOG_ENTER);
    }

    private String getLogLeave(PsiMethod psiMethod, PsiElement child) {
        return getLog(psiMethod, child, LogTypeEnum.LOG_LEAVE);

    }

    private String getLogError(PsiMethod psiMethod, String ex) {
        ReportConfigVO reportConfig = ToolSettings.getReportConfig();
        if (reportConfig != null && reportConfig.getLogTemplate() != null) {
            LogTemplateVO logTemplate = reportConfig.getLogTemplate();
            return String.format(logTemplate.getLogError(), "\"" + psiMethod.getName() + "\"", ex, ex);
        }
        return String.format(LogTypeEnum.LOG_ERROR.getCode(), "\"" + psiMethod.getName() + "\"", ex, ex);
    }

    private String getLogCommon(PsiMethod psiMethod, PsiType psiType, String text) {
        String variable = text;

        ReportConfigVO reportConfig = ToolSettings.getReportConfig();
        if (reportConfig != null && reportConfig.getLogTemplate() != null) {
            LogTemplateVO logTemplate = reportConfig.getLogTemplate();
            if (!StringUtils.isEmpty(logTemplate.getSerial())) {
                JavaTypeEnum javaTypeEnum = JavaTypeEnum.codeToEnum(psiType.getCanonicalText());
                if (javaTypeEnum == null || JavaTypeEnum.MAP_TYPE.equals(javaTypeEnum) || JavaTypeEnum.LIST_TYPE.equals(javaTypeEnum)) {
                    text = String.format(logTemplate.getSerial(), text);
                }
            }
            String logCommonStr = logTemplate.getLogCommon();
            int counts = 0;
            while (logCommonStr.length() > 0) {
                int index = logCommonStr.indexOf("%s");
                if (index > 0) {
                    logCommonStr = logCommonStr.replaceFirst("%s", "");
                    counts++;
                } else {
                    break;
                }
            }
            List<String> argsList = new ArrayList<>();
            if (counts == 0) {
            } else if (counts == 1) {
                argsList.add("\"" + psiMethod.getName() + "\"");
            } else if (counts == 2) {
                argsList.add("\"" + variable + "\"");
                argsList.add(text);
            } else {
                argsList.add("\"" + psiMethod.getName() + "\"");
                argsList.add("\"" + variable + "\"");
                argsList.add(text);
            }
            while (argsList.size() < counts) {
                argsList.add("");
            }
            return String.format(logTemplate.getLogCommon(), argsList.toArray());
        }
        return String.format(logCommon, psiMethod.getName(), variable, text);
    }

    public enum LogTypeEnum {
        LOG_ENTER(logEnter), LOG_LEAVE(logLeave), LOG_ERROR(logError);

        private String code;

        LogTypeEnum(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
