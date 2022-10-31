package com.idea.plugin.text;

import com.idea.plugin.document.support.JavaTypeEnum;
import com.idea.plugin.popup.module.ActionContext;
import com.idea.plugin.text.json.ToolMenu;
import com.idea.plugin.text.json.json5.Json5Generator;
import com.idea.plugin.text.mybatis.LogParser;
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.ClipboardUtils;
import com.idea.plugin.utils.JsonUtil;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonFormatService {

    public static final Pattern paramPattern = Pattern.compile("<([\\w, <>]+)>");
    Pattern fieldDocPattern = Pattern.compile("[\u4e00-\u9fa5 _a-zA-Z0-9]+");


    public String doGenerate(ActionContext context, TextFormatView instance) {
        String json = null;
        String text = null;
        if (context.getPsiElements() != null) {
            List<DbTable> dbTables = Arrays.stream(context.getPsiElements()).filter(it -> it instanceof DbTable).map(it -> (DbTable) it).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dbTables)) {
                if (dbTables.size() > 1) {
                    json = Json5Generator.dbsToJson(dbTables);
                } else {
                    json = Json5Generator.dbToJson(dbTables.get(0));
                }
                instance.switchMenu(ToolMenu.JSON);
                return json;
            }
        }
        if (context.getEditor() != null) {
            //鼠标选择类
            json = getAllFieldNames(context);
            if (json != null) {
                instance.switchMenu(ToolMenu.JSON);
                return json;
            }
            SelectionModel selectionModel = context.getEditor().getSelectionModel();
            text = selectionModel.getSelectedText();
            if (JsonUtil.isJson(text) || JsonUtil.isProperty(text)) {
                json = text;
            }
            if (StringUtils.isNotEmpty(LogParser.toSql(text))) {
                instance.switchMenu(ToolMenu.SQL);
                instance.switchMenu(instance.sqlMenu.getItemAt(2));
                return text;
            }
        }
        if (json == null) {
            if (context.getPsiFile() != null && context.getDocument() != null) {
                text = context.getDocument().getText();
                if (JsonUtil.isJson(text) || JsonUtil.isProperty(text)) {
                    json = text;
                }
            }
        }
        if (json == null) {
            text = ClipboardUtils.getClipboardText();
            if (JsonUtil.isJson(text) || JsonUtil.isProperty(text)) {
                json = text;
            }
            if (StringUtils.isNotEmpty(LogParser.toSql(text))) {
                instance.switchMenu(ToolMenu.SQL);
                instance.switchMenu(instance.sqlMenu.getItemAt(2));
                return text;
            }
        }
        if (json != null) {
            if (JsonUtil.isJson(json)) {
                json = JsonUtil.prettyJson(json);
            } else if (JsonUtil.isProperty(json)) {
                json = JsonUtil.prettyJson(JsonUtil.propertyToJson(json));
            }
            instance.switchMenu(ToolMenu.JSON);
            return json;
        } else {
            if (StringUtils.isNotEmpty(LogParser.toSql(text))) {
                instance.switchMenu(ToolMenu.SQL);
                instance.switchMenu(instance.sqlMenu.getItemAt(2));
                return text;
            }
            instance.switchMenu(ToolMenu.JSON);
            return text;
        }
    }

    public String getAllFieldNames(ActionContext context) {
        PsiClass psiClass = null;
        PsiElement psiElement = context.getPsiElement();
        // 鼠标定位到类
        if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) context.getPsiElement();
        }
        // 鼠标定位到属性
        if (psiElement instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) psiElement;
            String clazzParamName = psiLocalVariable.getName();
            // 通过光标步长递进找到属性名称
            PsiFile psiFile = context.getPsiFile();
            Editor editor = context.getEditor();
            int offsetStep = context.getOffset() - 1;
            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt || elementAt.getText().equals(clazzParamName) || elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(--offsetStep);
            }
            String clazzName = elementAt.getText();
            PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(clazzName, GlobalSearchScope.projectScope(context.getProject()));
            if (psiClasses.length > 0) {
                psiClass = psiClasses[0];
            }
        }
        if (psiClass == null) {
            return null;
        }
        PsiType classType = PsiTypesUtil.getClassType(psiClass);
        return Json5Generator.clazzToJson(classType, TranslatorFactroy.translate(psiClass.getName()));
    }

    public Map<String, Object> getAllFieldNames(ActionContext context, PsiClass psiClass, AtomicInteger downcount) {
        Map<String, Object> fieldNameMap = new LinkedHashMap<>();
        if (downcount.incrementAndGet() > 2) {
            downcount.set(0);
            return fieldNameMap;
        }
        PsiField[] allFields = psiClass.getAllFields();
        if (allFields.length == 0) {
            return fieldNameMap;
        }
        for (PsiField field : allFields) {
            if (field instanceof PsiEnumConstant) {
                continue;
            }
            if (field.hasModifierProperty(PsiModifier.STATIC) || field.hasModifierProperty(PsiModifier.FINAL)) {
                continue;
            }
            String name = field.getName();
            String clazzName = field.getType().getPresentableText();
            if (clazzName.startsWith(JavaTypeEnum.LIST_TYPE.getName())) {
                List<Object> subFieldNameList = new ArrayList<>();
                Matcher matcher = paramPattern.matcher(clazzName);
                if (matcher.find()) {
                    String subClazzName = matcher.group(1);
                    if (!psiClass.getName().equals(subClazzName)) {
                        Map<String, Object> fieldNames = getFieldNames(context, new LinkedHashMap<>(), field, subClazzName, downcount);
                        if (MapUtils.isNotEmpty(fieldNames)) {
                            subFieldNameList.add(fieldNames.get(name));
                        }
                    }
                }
                if (CollectionUtils.isEmpty(subFieldNameList)) {
                    subFieldNameList.add(TranslatorFactroy.translate(name));
                }
                fieldNameMap.put(name, subFieldNameList);
            } else if (clazzName.startsWith(JavaTypeEnum.MAP_TYPE.getName())) {
                Map<String, Object> subFieldNameMap = new LinkedHashMap<>();
                Matcher matcher = paramPattern.matcher(clazzName);
                if (matcher.find()) {
                    String subClazzName = matcher.group(1).split(",")[1].trim();
                    if (!psiClass.getName().equals(subClazzName)) {
                        Map<String, Object> fieldNames = getFieldNames(context, new LinkedHashMap<>(), field, subClazzName, downcount);
                        if (MapUtils.isNotEmpty(fieldNames)) {
                            subFieldNameMap = fieldNames;
                        }
                    }
                }
                if (MapUtils.isEmpty(subFieldNameMap)) {
                    subFieldNameMap.put(name, TranslatorFactroy.translate(name));
                }
                fieldNameMap.put(name, subFieldNameMap);
            } else {
                getFieldNames(context, fieldNameMap, field, clazzName, downcount);
            }
        }
        return fieldNameMap;
    }

    private Map<String, Object> getFieldNames(ActionContext context, Map<String, Object> fieldNameMap, PsiField field, String clazzName, AtomicInteger downcount) {
        String fieldNameDoc = null;
        if (field.getDocComment() != null) {
            Matcher matcher = fieldDocPattern.matcher(field.getDocComment().getText());
            while (matcher.find()) {
                String group = matcher.group();
                if (StringUtils.isNotBlank(group)) {
                    fieldNameDoc = group.trim();
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(fieldNameDoc)) {
            fieldNameDoc = TranslatorFactroy.translate(field.getName());
        }
        if (Arrays.stream(JavaTypeEnum.values()).anyMatch(javaTypeEnum -> clazzName.equals(javaTypeEnum.getName()))) {
            fieldNameMap.put(field.getName(), fieldNameDoc);
        } else {
            PsiClass[] psiClasses = PsiShortNamesCache.getInstance(context.getProject()).getClassesByName(clazzName, GlobalSearchScope.projectScope(context.getProject()));
            if (psiClasses.length > 0) {
                Map<String, Object> subFieldNameMap = getAllFieldNames(context, psiClasses[0], downcount);
                if (MapUtils.isEmpty(subFieldNameMap)) {
                    fieldNameMap.put(field.getName(), fieldNameDoc);
                } else {
                    fieldNameMap.put(field.getName(), subFieldNameMap);
                }
            }
        }
        return fieldNameMap;
    }


}
