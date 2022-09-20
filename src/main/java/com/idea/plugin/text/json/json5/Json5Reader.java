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
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.JsonUtil;
import com.idea.plugin.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Json5Reader {

    private static Pattern keypattern = Pattern.compile("\"(\\w+)\"");
    private static Pattern datepattern = Pattern.compile("^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}");

    public static JsonObject getFormatKeyDoc(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        JsonObject jsonObject = JsonUtil.fromJson(json, JsonObject.class);
        toJsonKeyDoc(jsonObject, json5Comment);
        return jsonObject;
    }

    public static String getKeyDoc(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        JsonObject jsonObject = JsonUtil.fromJson(json, JsonObject.class);
        StringBuilder keyDoc = new StringBuilder();
        toKeyDoc(jsonObject, json5Comment, keyDoc);
        return keyDoc.toString();
    }


    public static String getJson5(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        JsonObject jsonObject = JsonUtil.fromJson(json, JsonObject.class);
        return Json5Generator.objToJson(jsonObject, json5Comment);
    }

    public static String getMysqlDDL(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        JsonObject jsonObject = JsonUtil.fromJson(json, JsonObject.class);
        return toMysqlDDL(jsonObject, json5Comment);
    }

    public static String getOracleDDL(String json) {
        Map<String, String> json5Comment = getJson5Comment(json);
        JsonObject jsonObject = JsonUtil.fromJson(json, JsonObject.class);
        return toOraclDDL(jsonObject, json5Comment);
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
                    commentMap.put("rootComment", comment);
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

    public static String toMysqlDDL(Object object, Map<String, String> json5Comment) {
        String procedure = MysqlProcedureAddTable.addTableProcedure;
        if (object instanceof JsonObject) {
            final boolean[] hasPrimary = {false};
            int length = ((JsonObject) object).entrySet().stream().map(entry -> StringUtil.textToConstant(entry.getKey())).max(Comparator.comparing(String::length)).get().length();
            String call = ((JsonObject) object).entrySet().stream().map(entry -> {
                JsonElement value = entry.getValue();
                FieldTypeEnum fieldTypeEnum = FieldTypeEnum.VARCHAR;
                if (value.isJsonPrimitive()) {
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
                        }
                    }
                }
                String translate;
                if (json5Comment.containsKey(entry.getKey())) {
                    translate = json5Comment.get(entry.getKey());
                } else {
                    translate = TranslatorFactroy.translate(entry.getKey());
                }
                String format = String.format(MysqlProcedureAddTable.addTableCall, getColumnName(StringUtil.textToConstant(entry.getKey()), length), fieldTypeEnum.getMtype(FieldTypeEnum.codeGetArgs(fieldTypeEnum.name())), NullTypeEnum.NULL.getCode(), translate);
                if (!hasPrimary[0]) {
                    hasPrimary[0] = true;
                    format = format + PrimaryTypeEnum.PRIMARY.getCode();
                }
                return format;
            }).collect(Collectors.joining(",\n"));
            String tableName;
            String tableComment;
            if (json5Comment.containsKey("rootComment")) {
                tableComment = json5Comment.get("rootComment");
                tableName = StringUtil.textToConstant(TranslatorFactroy.translate(tableComment));
            } else {
                tableName = "T_TABLE_NAME";
                tableComment = "示例表";
            }
            return String.format(procedure, tableName, call, tableComment);
        }
        return "";
    }

    public static String toOraclDDL(Object object, Map<String, String> json5Comment) {
        String procedure = OracleProcedureAddTable.addTableProcedure;
        if (object instanceof JsonObject) {
            String tableName;
            String tableComment;
            if (json5Comment.containsKey("rootComment")) {
                tableComment = json5Comment.get("rootComment");
                tableName = StringUtil.textToConstant(TranslatorFactroy.translate(tableComment));
            } else {
                tableName = "T_TABLE_NAME";
                tableComment = "示例表";
            }
            final boolean[] hasPrimary = {false};
            int length = ((JsonObject) object).entrySet().stream().map(entry -> StringUtil.textToConstant(entry.getKey())).max(Comparator.comparing(String::length)).get().length();
            String call = ((JsonObject) object).entrySet().stream().map(entry -> {
                JsonElement value = entry.getValue();
                FieldTypeEnum fieldTypeEnum = FieldTypeEnum.VARCHAR;
                if (value.isJsonPrimitive()) {
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
                        }
                    }
                }
                String format = String.format(OracleProcedureAddTable.addTableCall, getColumnName(StringUtil.textToConstant(entry.getKey()), length), fieldTypeEnum.getOtype(FieldTypeEnum.codeGetArgs(fieldTypeEnum.name())), NullTypeEnum.NULL.getCode());
                if (!hasPrimary[0]) {
                    hasPrimary[0] = true;
                    format = format + PrimaryTypeEnum.PRIMARY.getCode();
                    format = format + String.format("\n                    CONSTRAINT %s_PK PRIMARY KEY", tableName);

                }
                return format;
            }).collect(Collectors.joining(",\n"));
            String callComment = ((JsonObject) object).entrySet().stream().map(entry -> {
                String translate;
                if (json5Comment.containsKey(entry.getKey())) {
                    translate = json5Comment.get(entry.getKey());
                } else {
                    translate = TranslatorFactroy.translate(entry.getKey());
                }
                return String.format(OracleProcedureAddTable.addTableCallComment, tableName, StringUtil.textToConstant(entry.getKey()), translate);
            }).collect(Collectors.joining("\n"));
            return String.format(procedure, tableName, tableName, call, tableName, tableComment, callComment);
        }
        return "";
    }

    private static String getColumnName(String columnName, Integer length) {
        for (int i = columnName.length(); i < length; i++) {
            columnName += " ";
        }
        return columnName;
    }
}
