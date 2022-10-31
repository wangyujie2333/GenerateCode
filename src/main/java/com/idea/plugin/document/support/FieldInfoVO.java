package com.idea.plugin.document.support;

import com.google.common.base.CaseFormat;
import com.idea.plugin.translator.TranslatorFactroy;
import com.idea.plugin.utils.StringUtil;
import com.intellij.psi.PsiField;
import org.apache.commons.lang3.StringUtils;

public class FieldInfoVO {

    /**
     * ψ场
     */
    public PsiField psiField;
    public String fieldName;
    public String fieldComent;
    public String columnName;
    public String fieldType;
    public String fieldJdbcType;

    public FieldInfoVO() {
    }

    public FieldInfoVO(com.idea.plugin.sql.support.FieldInfoVO fieldInfoVO) {
        this.fieldName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldInfoVO.getColumnName());
        this.fieldComent = fieldInfoVO.getComment();
        this.columnName = fieldInfoVO.getColumnName();
        this.fieldType = fieldInfoVO.getColumnType().getJclass().getName();
        this.fieldJdbcType = fieldInfoVO.getColumnType().getJtype().name();
    }


    public PsiField getPsiField() {
        return psiField;
    }

    public void setPsiField(PsiField psiField) {
        this.psiField = psiField;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldComent() {
        String coment = fieldComent;
        if (psiField != null && psiField.getDocComment() != null) {
            coment = psiField.getDocComment().getText();
        }
        if (StringUtils.isEmpty(coment)) {
            coment = TranslatorFactroy.translate(fieldName);
        }
        return coment;
    }

    public void setFieldComent(String fieldComent) {
        this.fieldComent = fieldComent;
    }

    public String getColumnName() {
        String columnName = this.columnName;
        if (StringUtils.isEmpty(columnName)) {
            columnName = StringUtil.textToCamelCase(fieldName,true);
        }
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldType() {
        if (psiField != null) {
            return psiField.getType().getCanonicalText();
        }
        return fieldType;
    }

    public String getFieldJdbcType() {
        if (psiField != null) {
            return JavaTypeEnum.getJdbcType(getFieldType()).name();
        }
        return fieldJdbcType;
    }


    public void setFieldJdbcType(String fieldJdbcType) {
        this.fieldJdbcType = fieldJdbcType;
    }
}
