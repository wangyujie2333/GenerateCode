package com.idea.plugin.text.json.json5;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.idea.plugin.sql.mysql.MysqlProcedureAddTable;
import com.idea.plugin.sql.oracle.OracleProcedureAddTable;
import com.idea.plugin.sql.support.enums.FieldTypeEnum;
import com.idea.plugin.sql.support.enums.NullTypeEnum;
import com.idea.plugin.sql.support.enums.PrimaryTypeEnum;
import com.idea.plugin.text.json.ToolMenu;
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.JsonUtil;
import com.idea.plugin.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Json5Reader {

    private static Pattern keypattern = Pattern.compile("\"(\\w+)\"");
    private static Pattern datepattern = Pattern.compile("^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}");

    public static Object getFormatKeyDoc(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        Object jsonObject = JsonUtil.fromJson(json);
        toJsonKeyDoc(jsonObject, json5Comment);
        return jsonObject;
    }

    public static String getKeyDoc(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        StringBuilder keyDoc = new StringBuilder();
        toKeyDoc(JsonUtil.fromJson(json), json5Comment, keyDoc);
        return keyDoc.toString();
    }


    public static String getJson5(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        return Json5Generator.objToJson(JsonUtil.fromJson(json), json5Comment);
    }

    public static String getMysqlDDL(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        return toMysqlDDL(JsonUtil.fromJson(json), json5Comment, "JsonRoot");
    }

    public static String getOracleDDL(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        return toOraclDDL(JsonUtil.fromJson(json), json5Comment, "JsonRoot");
    }

    public static Map<String, String> getJson5Comment(String json) {
        Map<String, String> commentMap = new HashMap<>();
        if (!isJson5(json)) {
            return commentMap;
        }
        String[] jsonArr = json.split("\n");
        for (int i = 0; i < jsonArr.length; i++) {
            String comment = jsonArr[i];
            if (comment.trim().startsWith("//")) {
                comment = comment.replaceAll("//", "").trim();
                String field = jsonArr[i + 1];
                if (field.trim().startsWith("{")) {
                    commentMap.put("JsonRoot", comment);
                } else {
                    Matcher matcher = keypattern.matcher(field);
                    if (matcher.find()) {
                        i++;
                        field = matcher.group(1);
                    }
                    commentMap.put(field, comment);
                }
            }
        }
        return commentMap;
    }

    public static boolean isJson5(String json) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        return json.trim().startsWith("//");
    }

    public static void toJsonKeyDoc(Object object, Map<String, String> json5Comment) {
        if (object instanceof JsonPrimitive) {
            return;
        }
        if (object instanceof JsonObject) {
            ((JsonObject) object).entrySet().forEach(entry -> {
                if (entry.getValue() instanceof JsonPrimitive) {
                    String translate;
                    if (json5Comment.containsKey(entry.getKey())) {
                        translate = json5Comment.get(entry.getKey());
                    } else {
                        translate = TranslatorFactroy.translate(entry.getKey());
                    }
                    ((JsonObject) object).addProperty(entry.getKey(), translate);
                } else if (entry.getValue() instanceof JsonArray) {
                    for (JsonElement jsonElement : ((JsonArray) entry.getValue())) {
                        if (jsonElement instanceof JsonPrimitive) {
                            String translate;
                            if (json5Comment.containsKey(entry.getKey())) {
                                translate = json5Comment.get(entry.getKey());
                            } else {
                                translate = TranslatorFactroy.translate(entry.getKey());
                            }
                            ((JsonArray) entry.getValue()).remove(0);
                            ((JsonArray) entry.getValue()).add(translate);
                            ((JsonObject) object).add(entry.getKey(), entry.getValue());
                            return;
                        }
                        toJsonKeyDoc(jsonElement, json5Comment);
                    }
                } else if (entry.getValue() instanceof JsonObject) {
                    toJsonKeyDoc(entry.getValue(), json5Comment);
                }
            });
        }
    }

    public static void toKeyDoc(Object object, Map<String, String> json5Comment, StringBuilder keyDoc) {
        if (object instanceof JsonPrimitive) {
            return;
        }
        if (object instanceof JsonObject) {
            ((JsonObject) object).entrySet().forEach(entry -> {
                if (entry.getValue() instanceof JsonPrimitive) {
                    String translate;
                    if (json5Comment.containsKey(entry.getKey())) {
                        translate = json5Comment.get(entry.getKey());
                    } else {
                        translate = TranslatorFactroy.translate(entry.getKey());
                    }
                    keyDoc.append(entry.getKey()).append(": ").append(translate).append("\n");
                } else if (entry.getValue() instanceof JsonArray) {
                    for (JsonElement jsonElement : ((JsonArray) entry.getValue())) {
                        if (jsonElement instanceof JsonPrimitive) {
                            String translate;
                            if (json5Comment.containsKey(entry.getKey())) {
                                translate = json5Comment.get(entry.getKey());
                            } else {
                                translate = TranslatorFactroy.translate(entry.getKey());
                            }
                            keyDoc.append(entry.getKey()).append(": ").append(translate).append("\n");
                            return;
                        }
                        toKeyDoc(jsonElement, json5Comment, keyDoc);
                    }
                } else if (entry.getValue() instanceof JsonObject) {
                    toKeyDoc(entry.getValue(), json5Comment, keyDoc);
                }
            });
        }
    }

    public static String toMysqlDDL(Object object, Map<String, String> json5Comment, String tableNameKey) {
        String procedure = MysqlProcedureAddTable.addTableProcedure;
        StringBuilder createTableStr = new StringBuilder();
        List<String> createTableNames = new ArrayList<>();
        if (object instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) object;
            if (jsonArray.get(0) instanceof JsonObject) {
                getMCreatTableStr((JsonObject) jsonArray.get(0), json5Comment, tableNameKey, procedure, createTableStr, createTableNames);
            }
        }
        if (object instanceof JsonObject) {
            getMCreatTableStr((JsonObject) object, json5Comment, tableNameKey, procedure, createTableStr, createTableNames);
        }
        return createTableStr.toString();
    }

    private static void getMCreatTableStr(JsonObject object, Map<String, String> json5Comment, String tableNameKey, String procedure, StringBuilder createTableStr, List<String> createTableNames) {
        String tableName = StringUtil.textToConstant(tableNameKey);
        String tableComment;
        if (json5Comment.containsKey(tableNameKey)) {
            tableComment = json5Comment.get(tableNameKey);
        } else {
            tableComment = TranslatorFactroy.translate(tableNameKey);
        }
        if (createTableNames.contains(tableName)) {
            return;
        }
        createTableNames.add(tableName);
        StringBuilder subCreateTableStr = new StringBuilder();
        final boolean[] hasPrimary = {false};
        int length = object.entrySet().stream().map(entry -> StringUtil.textToConstant(entry.getKey())).max(Comparator.comparing(String::length)).get().length();
        String call = object.entrySet().stream().map(entry -> {
            JsonElement value = entry.getValue();
            FieldTypeEnum fieldTypeEnum = FieldTypeEnum.VARCHAR;
            String args = FieldTypeEnum.codeGetArgs(fieldTypeEnum.name());
            if (value instanceof JsonArray || value instanceof JsonObject) {
                String tableStr = toMysqlDDL(value, json5Comment, entry.getKey());
                subCreateTableStr.append(tableStr);
                if (StringUtils.isEmpty(tableStr)) {
                    args = String.valueOf(512);
                }
            } else if (value.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) value;
                if (jsonPrimitive.isNumber()) {
                    fieldTypeEnum = FieldTypeEnum.NUMBER;
                } else {
                    if (jsonPrimitive.isString()) {
                        String valueStr = value.getAsString();
                        if (valueStr.length() >= 8 && valueStr.length() <= 20) {
                            Matcher matcher = datepattern.matcher(valueStr);
                            if (matcher.find()) {
                                fieldTypeEnum = FieldTypeEnum.TIMESTAMP;
                            }
                        }
                        int len = valueStr.length() > 32 ? valueStr.length() > 64 ? valueStr.length() > 512 ? 1024 : 512 : 64 : 32;
                        args = String.valueOf(len);
                    }
                }
            }
            String translate;
            if (json5Comment.containsKey(entry.getKey())) {
                translate = json5Comment.get(entry.getKey());
            } else {
                translate = TranslatorFactroy.translate(entry.getKey());
            }
            String format = String.format(MysqlProcedureAddTable.addTableCall, getColumnName(StringUtil.textToConstant(entry.getKey()), length), fieldTypeEnum.getMtype(args), NullTypeEnum.NULL.getCode(), translate);
            if (!hasPrimary[0]) {
                hasPrimary[0] = true;
                format = format + PrimaryTypeEnum.PRIMARY.getCode();
            }
            return format;
        }).collect(Collectors.joining(",\n"));
        createTableStr.append(String.format(procedure, tableName, call, tableComment)).append("\n");
        createTableStr.append(subCreateTableStr);
    }

    public static String toOraclDDL(Object object, Map<String, String> json5Comment, String tableNameKey) {
        String procedure = OracleProcedureAddTable.addTableProcedure;
        StringBuilder createTableStr = new StringBuilder();
        List<String> createTableNames = new ArrayList<>();
        if (object instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) object;
            if (jsonArray.get(0) instanceof JsonObject) {
                getOCreatTableStr((JsonObject) jsonArray.get(0), json5Comment, tableNameKey, procedure, createTableStr, createTableNames);
            }
        }
        if (object instanceof JsonObject) {
            getOCreatTableStr((JsonObject) object, json5Comment, tableNameKey, procedure, createTableStr, createTableNames);
        }
        return createTableStr.toString();
    }

    private static void getOCreatTableStr(JsonObject object, Map<String, String> json5Comment, String tableNameKey, String procedure, StringBuilder createTableStr, List<String> createTableNames) {
        String tableName = StringUtil.textToConstant(tableNameKey);
        String tableComment;
        if (json5Comment.containsKey(tableNameKey)) {
            tableComment = json5Comment.get(tableNameKey);
        } else {
            tableComment = TranslatorFactroy.translate(tableNameKey);
        }
        if (createTableNames.contains(tableName)) {
            return;
        }
        createTableNames.add(tableName);
        StringBuilder subCreateTableStr = new StringBuilder();
        final boolean[] hasPrimary = {false};
        int length = object.entrySet().stream().map(entry -> StringUtil.textToConstant(entry.getKey())).max(Comparator.comparing(String::length)).get().length();
        String call = object.entrySet().stream().map(entry -> {
            JsonElement value = entry.getValue();
            FieldTypeEnum fieldTypeEnum = FieldTypeEnum.VARCHAR;
            String args = FieldTypeEnum.codeGetArgs(fieldTypeEnum.name());
            if (value instanceof JsonArray || value instanceof JsonObject) {
                String tableStr = toOraclDDL(value, json5Comment, entry.getKey());
                subCreateTableStr.append(tableStr);
                if (StringUtils.isEmpty(tableStr)) {
                    args = String.valueOf(512);
                }
            } else if (value.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) value;
                if (jsonPrimitive.isNumber()) {
                    fieldTypeEnum = FieldTypeEnum.NUMBER;
                } else {
                    if (jsonPrimitive.isString()) {
                        String valueStr = value.getAsString();
                        if (valueStr.length() >= 8 && valueStr.length() <= 20) {
                            Matcher matcher = datepattern.matcher(valueStr);
                            if (matcher.find()) {
                                fieldTypeEnum = FieldTypeEnum.TIMESTAMP;
                            }
                        }
                        int len = valueStr.length() > 32 ? valueStr.length() > 64 ? valueStr.length() > 512 ? 1024 : 512 : 64 : 32;
                        args = String.valueOf(len);
                    }
                }
            }
            String format = String.format(OracleProcedureAddTable.addTableCall, getColumnName(StringUtil.textToConstant(entry.getKey()), length), fieldTypeEnum.getOtype(args), NullTypeEnum.NULL.getCode());
            if (!hasPrimary[0]) {
                hasPrimary[0] = true;
                format = format + PrimaryTypeEnum.PRIMARY.getCode();
                format = format + String.format("\n                    CONSTRAINT %s_PK PRIMARY KEY", tableName);
            }
            return format;
        }).collect(Collectors.joining(",\n"));
        String callComment = object.entrySet().stream().map(entry -> {
            String translate;
            if (json5Comment.containsKey(entry.getKey())) {
                translate = json5Comment.get(entry.getKey());
            } else {
                translate = TranslatorFactroy.translate(entry.getKey());
            }
            return String.format(OracleProcedureAddTable.addTableCallComment, tableName, StringUtil.textToConstant(entry.getKey()), translate);
        }).collect(Collectors.joining("\n"));
        createTableStr.append(String.format(procedure, tableName, tableName, call, tableName, tableComment, callComment)).append("\n");
        createTableStr.append(subCreateTableStr);
    }

    private static String getColumnName(String columnName, Integer length) {
        for (int i = columnName.length(); i < length; i++) {
            columnName += " ";
        }
        return columnName;
    }

    public static String getInsertDML(String json, String procedure, ToolMenu insertType) {
        Object object = JsonUtil.fromJson(json);
        Map<String, String> json5Comment = getJson5Comment(json);
        String tableName = "JSON_ROOT_TABLE";
        if (json5Comment.containsKey("JsonRoot")) {
            tableName = StringUtil.textToConstant(TranslatorFactroy.translate(json5Comment.get("JsonRoot")));
        }
        return getMIsertSql(object, procedure, insertType, tableName);
    }

    @NotNull
    private static String getMIsertSql(Object object, String procedure, ToolMenu insertType, String tableName) {
        StringBuilder insertSqlStr = new StringBuilder();
        if (object instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) object;
            for (JsonElement jsonElement : jsonArray) {
                if (jsonElement instanceof JsonObject) {
                    getMInsertSqlStr((JsonObject) jsonElement, tableName, procedure, insertType, insertSqlStr);
                }
            }
        }
        if (object instanceof JsonObject) {
            getMInsertSqlStr((JsonObject) object, tableName, procedure, insertType, insertSqlStr);
        }
        return insertSqlStr.toString();
    }

    private static void getMInsertSqlStr(JsonObject object, String tableName, String procedure, ToolMenu insertType, StringBuilder insertSqlStr) {
        StringBuilder subinsertSqlStr = new StringBuilder();
        List<String> codeList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        object.entrySet().forEach(entry -> {
            JsonElement value = entry.getValue();
            String keyName = StringUtil.textToConstant(entry.getKey());
            Object valueObj;
            if (value instanceof JsonArray || value instanceof JsonObject) {
                String tableStr = getMIsertSql(value, procedure, insertType, keyName);
                subinsertSqlStr.append(tableStr);
                if (StringUtils.isEmpty(tableStr)) {
                    valueObj = JsonUtil.toJson(value);
                } else {
                    valueObj = "PK_" + entry.getKey();
                }
            } else if (value.isJsonPrimitive()) {
                valueObj = JsonUtil.getPrimitiveValue((JsonPrimitive) value);
                if (valueObj instanceof String) {
                    String valueStr = (String) valueObj;
                    valueStr = valueStr.replaceAll("'", "\\\\'");
                    if (valueStr.length() >= 8 && valueStr.length() <= 20) {
                        Matcher matcher = datepattern.matcher(valueStr);
                        if (matcher.find()) {
                            if (ToolMenu.SQL_INSERT_O.equals(insertType)) {
                                valueStr = "TIMESTAMP " + valueStr;
                            }
                        }
                    }
                    valueObj = valueStr;
                }
            } else {
                valueObj = value.getAsString();
            }
            valueList.add(valueObj);
            codeList.add(keyName);
        });
        String values = valueList.stream().map(o -> {
            if (o == null || o.equals("null")) {
                return "null";
            }
            if (o instanceof String) {
                return "'" + o + "'";
            } else {
                return o + "";
            }
        }).collect(Collectors.joining(", "));
        String codes = String.join(", ", codeList);
        if (ToolMenu.SQL_INSERT.equals(insertType)) {
            insertSqlStr.append(String.format(procedure, tableName, codes, values));
        } else {
            insertSqlStr.append(String.format(procedure, tableName, codes, values, tableName, codeList.get(0), valueList.get(0)));
        }
        insertSqlStr.append("\n");
        insertSqlStr.append(subinsertSqlStr);
    }

}
