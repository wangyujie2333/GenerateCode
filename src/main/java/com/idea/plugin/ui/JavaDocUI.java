package com.idea.plugin.ui;

import com.idea.plugin.document.support.JavaDocVO;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JavaDocUI extends DialogWrapper {

    private JCheckBox isCCovered;
    private JCheckBox isCGenerate;
    private JCheckBox isCParent;
    private JCheckBox isMCovered;
    private JCheckBox isMGenerate;
    private JCheckBox isMParent;
    private JCheckBox isFCovered;
    private JCheckBox isFGenerate;
    private JCheckBox isFParent;
    private JCheckBox isCInner;
    private JCheckBox isMInner;
    private JCheckBox isFInner;
    private JPanel jPanel;

    public static JavaDocUI getInstance(Project project) {
        return new JavaDocUI(project);
    }

    public JavaDocUI(@Nullable Project project) {
        super(project);
        super.init();
        setJavaDocVO();
        setTitle("请选择注释属性");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return jPanel;
    }

    public JavaDocVO getJavaDocVO() {
        JavaDocVO javaDocVO = JavaDocVO.getInstance();
        javaDocVO.setCCovered(isCCovered.isSelected());
        javaDocVO.setCGenerate(isCGenerate.isSelected());
        javaDocVO.setCParent(isCParent.isSelected());
        javaDocVO.setMCovered(isMCovered.isSelected());
        javaDocVO.setMGenerate(isMGenerate.isSelected());
        javaDocVO.setMParent(isMParent.isSelected());
        javaDocVO.setFCovered(isFCovered.isSelected());
        javaDocVO.setFGenerate(isFGenerate.isSelected());
        javaDocVO.setFParent(isFParent.isSelected());
        javaDocVO.setCInner(isCInner.isSelected());
        javaDocVO.setMInner(isMInner.isSelected());
        javaDocVO.setFInner(isFInner.isSelected());
        return javaDocVO;
    }

    public void setJavaDocVO() {
        JavaDocVO javaDocVO = JavaDocVO.getInstance();
        isCCovered.setSelected(javaDocVO.getCCovered());
        isCGenerate.setSelected(javaDocVO.getCGenerate());
        isCParent.setSelected(javaDocVO.getCParent());
        isMCovered.setSelected(javaDocVO.getMCovered());
        isMGenerate.setSelected(javaDocVO.getMGenerate());
        isMParent.setSelected(javaDocVO.getMParent());
        isFCovered.setSelected(javaDocVO.getFCovered());
        isFGenerate.setSelected(javaDocVO.getFGenerate());
        isFParent.setSelected(javaDocVO.getFParent());
        isCInner.setSelected(javaDocVO.getCInner());
        isMInner.setSelected(javaDocVO.getMInner());
        isFInner.setSelected(javaDocVO.getFInner());
    }
}
