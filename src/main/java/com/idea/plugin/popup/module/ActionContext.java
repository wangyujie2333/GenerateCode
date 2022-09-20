package com.idea.plugin.popup.module;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActionContext {

    public static Project globalproject;
    /**
     * 工程对象
     */
    private Project project;
    /**
     * 文件
     */
    private PsiFile psiFile;
    /**
     * psi文件路径
     */
    private String psiFilePath;
    /**
     * 选中文件
     */
    private VirtualFile[] virtualFiles;

    /**
     * 数据上下文
     */
    private DataContext dataContext;
    /**
     * 编辑器
     */
    private Editor editor;
    /**
     * 元素
     */
    private PsiElement psiElement;
    private PsiElement[] psiElements;
    /**
     * 位点
     */
    private Integer offset;
    /**
     * 文档
     */
    private Document document;
    /**
     * 行号
     */
    private Integer lineNumber;
    /**
     * 开始位置
     */
    private Integer startOffset;
    /**
     * 文本编辑
     */
    private String editorText;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public PsiFile getPsiFile() {
        return psiFile;
    }

    public void setPsiFile(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public String getPsiFilePath() {
        return psiFilePath;
    }

    public void setPsiFilePath(String psiFilePath) {
        this.psiFilePath = psiFilePath;
    }

    public VirtualFile[] getVirtualFiles() {
        return virtualFiles;
    }

    public void setVirtualFiles(VirtualFile[] virtualFiles) {
        this.virtualFiles = virtualFiles;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

    public void setPsiElement(PsiElement psiElement) {
        this.psiElement = psiElement;
    }

    public PsiElement[] getPsiElements() {
        return psiElements;
    }

    public void setPsiElements(PsiElement[] psiElements) {
        this.psiElements = psiElements;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
        this.startOffset = startOffset;
    }

    public String getEditorText() {
        return editorText;
    }

    public void setEditorText(String editorText) {
        this.editorText = editorText;
    }

    public List<String> getTextList() {
        if (StringUtils.isEmpty(editorText)) {
            return null;
        }
        return Arrays.stream(editorText.split("\n")).collect(Collectors.toList());
    }

    public boolean isPsiFilePathSuffix(String suffix) {
        if (this.virtualFiles == null || this.virtualFiles.length == 0) {
            return false;
        }
        if (this.virtualFiles.length > 1) {
            return false;
        }
        if (this.psiFilePath == null) {
            return false;
        }
        return this.psiFilePath.toLowerCase().endsWith(suffix);
    }

}
