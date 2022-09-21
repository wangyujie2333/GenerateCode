package com.idea.plugin.ui;

import com.idea.plugin.document.support.JavaSetGetVO;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JavaSetGetUI extends DialogWrapper {
    private JCheckBox isNoneConstructor;
    private JCheckBox isAllConstructor;
    private JCheckBox isSet;
    private JCheckBox isGet;
    private JPanel jPanel;
    private JCheckBox isSGParent;
    private JCheckBox isSGInner;

    public static JavaSetGetUI getInstance(Project project) {
        return new JavaSetGetUI(project);
    }

    public JavaSetGetUI(@Nullable Project project) {
        super(project);
        super.init();
        setJavaSetGetVO();
        setTitle("请选择生成方法");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return jPanel;
    }

    public JavaSetGetVO getJavaSetGetVO() {
        JavaSetGetVO javaSetGetVO = JavaSetGetVO.getInstance();
        javaSetGetVO.setIsNoneConstructor(isNoneConstructor.isSelected());
        javaSetGetVO.setIsAllConstructor(isAllConstructor.isSelected());
        javaSetGetVO.setIsSet(isSet.isSelected());
        javaSetGetVO.setIsGet(isGet.isSelected());
        javaSetGetVO.setIsSGParent(isSGParent.isSelected());
        javaSetGetVO.setIsSGInner(isSGInner.isSelected());
        return javaSetGetVO;
    }

    public void setJavaSetGetVO() {
        JavaSetGetVO javaSetGetVO = JavaSetGetVO.getInstance();
        isNoneConstructor.setSelected(javaSetGetVO.getIsNoneConstructor());
        isAllConstructor.setSelected(javaSetGetVO.getIsAllConstructor());
        isSet.setSelected(javaSetGetVO.getIsSet());
        isGet.setSelected(javaSetGetVO.getIsGet());
        isSGParent.setSelected(javaSetGetVO.getIsSGParent());
        isSGInner.setSelected(javaSetGetVO.getIsSGInner());
    }
}
