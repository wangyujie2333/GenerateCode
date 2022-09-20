package com.idea.plugin.document.support;

import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiModifier;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClazzInfoDOVO {

    public String tableName;
    public String clazzName;
    public String clazzVOName;

    public List<FieldInfoVO> fieldinfos = new ArrayList<>();
    public List<FieldInfoVO> vosFieldinfos = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getClazzVOName() {
        return clazzVOName;
    }

    public void setClazzVOName(String clazzVOName) {
        this.clazzVOName = clazzVOName;
    }

    public List<FieldInfoVO> getFieldinfos() {
        if (CollectionUtils.isNotEmpty(fieldinfos)) {
            return fieldinfos;
        }
        if (CollectionUtils.isNotEmpty(vosFieldinfos)) {
            return vosFieldinfos;
        }
        return fieldinfos;
    }

    public void setFieldinfos(List<FieldInfoVO> fieldinfos) {
        this.fieldinfos = fieldinfos.stream().filter(fieldInfoVO -> fieldInfoVO.getPsiField() == null || !(fieldInfoVO.getPsiField() instanceof PsiEnumConstant
                || fieldInfoVO.getPsiField().hasModifierProperty(PsiModifier.STATIC)
                || fieldInfoVO.getPsiField().hasModifierProperty(PsiModifier.FINAL))).collect(Collectors.toList());
    }

    public List<FieldInfoVO> getVosFieldinfos() {
        return vosFieldinfos;
    }

    public void setVosFieldinfos(List<FieldInfoVO> vosFieldinfos) {
        this.vosFieldinfos = vosFieldinfos.stream().filter(fieldInfoVO -> !(fieldInfoVO.getPsiField() instanceof PsiEnumConstant
                || fieldInfoVO.getPsiField().hasModifierProperty(PsiModifier.STATIC)
                || fieldInfoVO.getPsiField().hasModifierProperty(PsiModifier.FINAL))).collect(Collectors.toList());
    }
}
