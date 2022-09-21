package com.idea.plugin.document.support;

import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MethodInfoVO {

    public PsiMethod psiMethod;
    public String methodComment;
    public String methodReturn;
    public String methodName;
    public Map<String, String> methodParameter = new LinkedHashMap<>();
    public List<String> methodParameterInfos = new ArrayList<>();
    public List<String> methodThrowsList;

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public String getMethodComment() {
        return methodComment;
    }

    public void setMethodComment(String methodComment) {
        this.methodComment = methodComment;
    }

    public String getMethodReturn() {
        return methodReturn;
    }

    public void setMethodReturn(String methodReturn) {
        this.methodReturn = methodReturn;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, String> getMethodParameter() {
        return methodParameter;
    }

    public void setMethodParameter(Map<String, String> methodParameter) {
        this.methodParameter = methodParameter;
    }

    public List<String> getMethodParameterInfos() {
        return methodParameterInfos;
    }

    public void setMethodParameterInfos(List<String> methodParameterInfos) {
        this.methodParameterInfos = methodParameterInfos;
    }

    public List<String> getMethodThrowsList() {
        return methodThrowsList;
    }

    public void setMethodThrowsList(List<String> methodThrowsList) {
        this.methodThrowsList = methodThrowsList;
    }

    public void addMethodThrowsList(String methodThrows) {
        if (this.methodThrowsList == null) {
            this.methodThrowsList = new ArrayList<>();
        }
        this.methodThrowsList.add(methodThrows);
    }
}
