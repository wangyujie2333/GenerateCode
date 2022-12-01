package com.idea.plugin.text.json.json5;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.idea.plugin.sql.support.enums.FieldTypeEnum;
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.DBUtils;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.StringUtil;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Json5Generator {
    /**
     * 字段忽略注解
     */
    private final Set<String> ignores;
    /**
     * 字段序列化
     */
    private final Map<String, ApiDocObjectSerialPo> serials;
    private static final Logger LOG = Logger.getInstance(Json5Generator.class);
    private static final Consumer<Json5Writer> BEGIN_ARRAY = writer -> {
        try {
            writer.beginArray();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final Consumer<Json5Writer> END_ARRAY = writer -> {
        try {
            writer.endArray();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final Consumer<Json5Writer> BEGIN_OBJECT = writer -> {
        try {
            writer.beginObject();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final Consumer<Json5Writer> END_OBJECT = writer -> {
        try {
            writer.endObject();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final BiConsumer<Json5Writer, String> COMMENT = (writer, s) -> {
        try {
            writer.comment(s);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final BiConsumer<Json5Writer, Object> VALUE = (writer, o) -> {
        try {
            writer.value(o);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final BiConsumer<Json5Writer, String> NAME = (writer, s) -> {
        try {
            writer.name(s);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };

    Json5Generator(List<String> originIgnores, List<ApiDocObjectSerialPo> originSerials) {
        // object不能在校验范围
        this.ignores = Collections.unmodifiableSet(new HashSet<>(originIgnores));
        Map<String, ApiDocObjectSerialPo> collect = originSerials.stream()
                .collect(Collectors.toMap(ApiDocObjectSerialPo::getType, it -> it, (o, n) -> n));
        // 保证存在原型类型
        WebCopyConstants.PRIMITIVE_SERIALS.forEach(it -> collect.putIfAbsent(it.getType(), it.deepCopy()));
        this.serials = Collections.unmodifiableMap(collect);
    }

    /**
     * 将任意类型转为json5
     *
     * @param psiType     {@link PsiType}
     * @param rootComment 根注释
     * @return json5
     */
    public static String clazzToJson(PsiType psiType, String rootComment) {
        List<ApiDocObjectSerialPo> objects = new ArrayList<>();
        WebCopyConstants.PRIMITIVE_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        WebCopyConstants.WRAPPED_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        return new Json5Generator(WebCopyConstants.FIELD_IGNORE_ANNOTATIONS, Collections.unmodifiableList(objects)).toJson5(psiType, rootComment);
    }

    /**
     * 是否是忽略类型
     *
     * @param psiType {@link PsiType}
     * @return true/false
     */
    boolean isIgnore(PsiType psiType) {
        if (psiType instanceof PsiPrimitiveType) {
            // void类型忽略
            return void.class.getName().equals(((PsiPrimitiveType) psiType).getName());
        }

        if (!(psiType instanceof PsiClassType)) {
            return false;
        }

        PsiClass resolve = ((PsiClassType) psiType).resolve();
        // java.lang.Void类型忽略
        return resolve != null && Void.class.getName().equals(resolve.getQualifiedName());
    }

    public String toJson5(PsiType psiType, String rootComment) {
        if (this.isIgnore(psiType)) {
            return null;
        }

        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");

        // 根注释
        if (Objects.nonNull(rootComment) && !rootComment.trim().isEmpty()) {
            COMMENT.accept(writer, rootComment.trim());
        } else {
            COMMENT.accept(writer, TranslatorFactroy.translate(psiType.getCanonicalText()));
        }
        this.toJson5(writer, psiType, "$");

        return Optional.of(stringWriter.toString()).map(String::trim).filter(it -> it.length() > 0).orElse(null);
    }

    void toJson5(Json5Writer writer, PsiType type, String ref) {
        if (this.isIgnore(type)) {
            return;
        }

        // 数组类型
        if (type instanceof PsiArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) type;
            BEGIN_ARRAY.accept(writer);
            this.toJson5(writer, psiArrayType.getComponentType(), ref + "[0]");
            END_ARRAY.accept(writer);
            return;
        }

        if (type instanceof PsiPrimitiveType) {
            // 基本类型
            PsiPrimitiveType primitiveType = (PsiPrimitiveType) type;
            ApiDocObjectSerialPo po = serials.get(primitiveType.getName());
            VALUE.accept(writer, AliasType.value(po.getAlias(), po.getValue()));
            return;
        }

        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        // 不支持类型
        if (Objects.isNull(psiClass)) {
            return;
        }
        String className = psiClass.getQualifiedName();
        // 基本类型
        if (serials.containsKey(className)) {
            ApiDocObjectSerialPo po = serials.get(className);
            VALUE.accept(writer, AliasType.value(po.getAlias(), po.getValue()));
            return;
        }
        if (psiClass.isEnum()) {
            if (psiClass.getAllFields().length > 0) {
                PsiField psiField = psiClass.getAllFields()[0];
                VALUE.accept(writer, AliasType.STRING.deserialize(psiField.getName()));
            } else {
                BEGIN_OBJECT.accept(writer);
                END_OBJECT.accept(writer);
            }
            return;
        }
        // 否则则为复杂类型
        if (type instanceof PsiClassType) {
            PsiClassType referenceType = (PsiClassType) type;
            // 集合类型
            if (InheritanceUtil.isInheritor(type, Iterable.class.getName())) {
                BEGIN_ARRAY.accept(writer);
                // 若存在泛型参数
                if (referenceType.getParameterCount() > 0) {
                    this.toJson5(writer, referenceType.getParameters()[0], ref + "<0>");
                }
                END_ARRAY.accept(writer);
                return;
            }
            String suffix = String.format("@%s", referenceType.getCanonicalText());
            ref = ref + suffix;
            BEGIN_OBJECT.accept(writer);
            if (!psiClass.isInterface()) {
                Map<PsiTypeParameter, PsiType> substitutionMap = new HashMap<>(
                        referenceType.resolveGenerics().getSubstitutor().getSubstitutionMap()
                );
                // 若存在父类 添加父类的的泛型
                PsiClassType[] extendsListTypes = psiClass.getExtendsListTypes();
                for (PsiClassType extendsListType : extendsListTypes) {
                    substitutionMap.putAll(extendsListType.resolveGenerics().getSubstitutor().getSubstitutionMap());
                }
                String finalRef = ref;
                Arrays.stream(psiClass.getAllFields())
                        // 非static、transient字段
                        .filter(SpringWebPsiUtil::isValidFiled)
                        // 非注解标记字段
                        .filter(it -> AnnotationUtil.findAnnotations(it, this.ignores).length == 0)
                        // 去掉父类重复字段
                        .collect(Collectors.toMap(PsiField::getName, it -> it, (o, n) -> o))
                        .values()
                        .stream()
                        .sorted(Comparator.comparing(PsiField::getName))
                        .forEach(it -> this.fieldJson5(writer, it, substitutionMap, finalRef));
            }
            END_OBJECT.accept(writer);
        }
    }

    private void fieldJson5(Json5Writer writer, PsiField it, Map<PsiTypeParameter, PsiType> substitutionMap, String ref) {
        // 字段注释
        Optional<String> commentOptional = Optional.ofNullable(it.getDocComment())
                .map(PsiDocComment::getDescriptionElements)
                .map(els -> Arrays.stream(els)
                        .filter(e -> e instanceof PsiDocToken)
                        .map(PsiDocToken.class::cast)
                        .filter(SpringWebPsiUtil::isDocCommentData)
                        .map(e -> e.getText().trim())
                        .collect(Collectors.joining("")))
                .filter(comment -> !comment.isEmpty());
        if (commentOptional.isPresent()) {
            COMMENT.accept(writer, commentOptional.get());
        } else {
            COMMENT.accept(writer, TranslatorFactroy.translate(it.getName()));
        }
        NAME.accept(writer, it.getName());
        PsiType fieldType = it.getType();

        String suffix = String.format("@%s", it.getType().getCanonicalText());
        // 引用循环
        if (ref.contains(suffix)) {
            if (InheritanceUtil.isInheritor(fieldType, Iterable.class.getName())
                    || fieldType instanceof PsiArrayType) {
                BEGIN_ARRAY.accept(writer);
                END_ARRAY.accept(writer);
            } else {
                BEGIN_OBJECT.accept(writer);
                END_OBJECT.accept(writer);
            }
            return;
        }

        // 包装类型
        if (fieldType instanceof PsiClassReferenceType) {
            PsiClassReferenceType paramClassReferenceType = (PsiClassReferenceType) fieldType;
            // 集合类型
            if (InheritanceUtil.isInheritor(fieldType, Iterable.class.getName())) {
                BEGIN_ARRAY.accept(writer);
                // 若存在泛型参数
                if (paramClassReferenceType.getParameterCount() > 0
                        && paramClassReferenceType.getParameters()[0] instanceof PsiClassReferenceType) {
                    PsiClassType parameter = (PsiClassType) paramClassReferenceType.getParameters()[0];
                    PsiClass resolve = parameter.resolve();
                    if (resolve instanceof PsiTypeParameter && substitutionMap.containsKey(resolve)) {
                        this.toJson5(writer, substitutionMap.get(resolve), ref + suffix + "<0>");
                    } else {
                        this.toJson5(writer, parameter, ref + suffix + "<0>");
                    }
                }
                END_ARRAY.accept(writer);
                return;
            }

            PsiClass paramType = paramClassReferenceType.resolve();
            if (paramType instanceof PsiTypeParameter && substitutionMap.containsKey(paramType)) {
                PsiType psiType = substitutionMap.get(paramType);
                // 存在泛型类型为null的情况
                if (psiType != null) {
                    this.toJson5(writer, psiType, ref + suffix);
                    // 提前结束泛型参数处理
                    return;
                }
            }
        }

        this.toJson5(writer, fieldType, ref + suffix);
    }

    public static String objToJson(Object object, Map<String, String> json5Comment) {
        List<ApiDocObjectSerialPo> objects = new ArrayList<>();
        WebCopyConstants.PRIMITIVE_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        WebCopyConstants.WRAPPED_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        return new Json5Generator(WebCopyConstants.FIELD_IGNORE_ANNOTATIONS, Collections.unmodifiableList(objects)).toJson5(object, json5Comment, "jsonRoot");
    }

    boolean isIgnore(Class<?> clazz) {
        return false;
    }

    public String toJson5(Object object, Map<String, String> json5Comment, String rootComment) {
        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");
        // 根注释
        if (json5Comment.containsKey("JsonRoot")) {
            COMMENT.accept(writer, json5Comment.get("JsonRoot"));
        } else {
            COMMENT.accept(writer, rootComment.trim());
        }
        this.toJson5(writer, json5Comment, object);
        return Optional.of(stringWriter.toString()).map(String::trim).filter(it -> it.length() > 0).orElse(null);
    }

    void toJson5(Json5Writer writer, Map<String, String> json5Comment, Object object) {
        if (this.isIgnore(object.getClass())) {
            return;
        }
        // 基本类型
        if (object instanceof JsonPrimitive) {
            JsonPrimitive jsonPrimitive = (JsonPrimitive) object;
            Object value;
            if (jsonPrimitive.isBoolean()) {
                value = jsonPrimitive.getAsBoolean();
            } else if (jsonPrimitive.isNumber()) {
                value = jsonPrimitive.getAsBigDecimal();
            } else if (jsonPrimitive.isJsonNull()) {
                value = null;
            } else {
                value = jsonPrimitive.getAsString();
            }
            VALUE.accept(writer, value);
            return;
        }
        // 数组类型
        if (object instanceof JsonArray) {
            BEGIN_ARRAY.accept(writer);
            ((JsonArray) object).forEach(jsonElement -> this.toJson5(writer, json5Comment, jsonElement));
            END_ARRAY.accept(writer);
            return;
        }
        // 否则则为复杂类型
        if (object instanceof JsonObject) {
            BEGIN_OBJECT.accept(writer);
            ((JsonObject) object).entrySet().forEach(entry -> {
                if (json5Comment.containsKey(entry.getKey())) {
                    COMMENT.accept(writer, json5Comment.get(entry.getKey()));
                } else {
                    COMMENT.accept(writer, TranslatorFactroy.translate(entry.getKey()));
                }
                NAME.accept(writer, entry.getKey());
                this.toJson5(writer, json5Comment, entry.getValue());
            });
            END_OBJECT.accept(writer);
        }
    }

    public static String dbsToJson(List<DbTable> dbTables) {
        List<ApiDocObjectSerialPo> objects = new ArrayList<>();
        WebCopyConstants.PRIMITIVE_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        WebCopyConstants.WRAPPED_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        return new Json5Generator(WebCopyConstants.FIELD_IGNORE_ANNOTATIONS, Collections.unmodifiableList(objects)).toJson5(dbTables, "表转JSON");
    }

    public static String dbToJson(DbTable dbTable) {
        List<ApiDocObjectSerialPo> objects = new ArrayList<>();
        WebCopyConstants.PRIMITIVE_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        WebCopyConstants.WRAPPED_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        String comment = dbTable.getComment();
        if (StringUtils.isEmpty(comment)) {
            comment = TranslatorFactroy.translate(dbTable.getName());
        }
        return new Json5Generator(WebCopyConstants.FIELD_IGNORE_ANNOTATIONS, Collections.unmodifiableList(objects)).toJson5(dbTable, comment);
    }

    public String toJson5(List<DbTable> dbTables, String rootComment) {
        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");
        // 根注释
        if (Objects.nonNull(rootComment) && !rootComment.trim().isEmpty()) {
            COMMENT.accept(writer, rootComment.trim());
        }
        BEGIN_OBJECT.accept(writer);

        dbTables.forEach(dbTable -> {
            String comment = dbTable.getComment();
            if (StringUtils.isEmpty(comment)) {
                comment = TranslatorFactroy.translate(dbTable.getName());
            }
            COMMENT.accept(writer, comment);
            NAME.accept(writer, StringUtil.textToCamelCase(dbTable.getName(), false));
            this.toJson5(writer, dbTable);
        });
        END_OBJECT.accept(writer);
        return Optional.of(stringWriter.toString()).map(String::trim).filter(it -> it.length() > 0).orElse(null);
    }

    public String toJson5(DbTable dbTable, String rootComment) {
        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");
        // 根注释
        if (Objects.nonNull(rootComment) && !rootComment.trim().isEmpty()) {
            COMMENT.accept(writer, rootComment.trim());
        }
        this.toJson5(writer, dbTable);
        return Optional.of(stringWriter.toString()).map(String::trim).filter(it -> it.length() > 0).orElse(null);
    }

    void toJson5(Json5Writer writer, DbTable dbTable) {
        BEGIN_OBJECT.accept(writer);
        DasUtil.getColumns(dbTable)
                .forEach(it -> {
                    JDBCType jdbcType = DBUtils.convertToJdbcType(it.getDataType().typeName, dbTable.getDataSource().getDatabaseVersion().name);
                    if (jdbcType == null) {
                        jdbcType = JDBCType.VARCHAR;
                    }
                    FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getFieldTypeBySqlType(jdbcType.getVendorTypeNumber());
                    String comment = it.getComment();
                    if (StringUtils.isEmpty(comment)) {
                        comment = TranslatorFactroy.translate(it.getName());
                    }
                    COMMENT.accept(writer, comment);
                    String name = StringUtil.textToCamelCase(it.getName(), false);
                    NAME.accept(writer, name);
                    if (FieldTypeEnum.NUMBER.equals(fieldTypeEnum) || FieldTypeEnum.INTEGER.equals(fieldTypeEnum)) {
                        VALUE.accept(writer, "128");
                    } else if (FieldTypeEnum.DOUBLE.equals(fieldTypeEnum) || FieldTypeEnum.FLOAT.equals(fieldTypeEnum) || FieldTypeEnum.BIGINT.equals(fieldTypeEnum)) {
                        VALUE.accept(writer, "1024.0");
                    } else if (FieldTypeEnum.TIMESTAMP.equals(fieldTypeEnum) || FieldTypeEnum.TIME.equals(fieldTypeEnum) || FieldTypeEnum.DATE.equals(fieldTypeEnum)) {
                        VALUE.accept(writer, DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYY_MM_DD_HH_MM_SS));
                    } else {
                        if (it.getName().toLowerCase().endsWith("id")) {
                            VALUE.accept(writer, "ece9247cf9b1a464a43ef829ff3b9320");
                        } else {
                            VALUE.accept(writer, name + "String");
                        }
                    }
                });
        END_OBJECT.accept(writer);
    }

}
