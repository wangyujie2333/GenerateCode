package com.idea.plugin.document.support;

import com.intellij.psi.PsiField;

public class FieldInfoVO {

    /**
     * ψ场
     */
    public PsiField psiField;
    public String fieldName;

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

    public String getFieldType() {
        return psiField.getType().getCanonicalText();
    }

    public String getFieldJdbcType() {
        return JavaTypeEnum.getJdbcType(getFieldType()).name();
    }
}
