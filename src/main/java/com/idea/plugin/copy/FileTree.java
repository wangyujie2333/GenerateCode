package com.idea.plugin.copy;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件树
 *
 * @author jimmy
 * @date 2022-11-23 10:34:38
 */
public class FileTree {
    /**
     * 的名字
     */
    private String name;
    /**
     * 指数
     */
    private Integer index;
    /**
     * 父
     */
    private FileTree parent;
    /**
     * 孩子们
     */
    private List<FileTree> children;
    /**
     * 叶
     */
    private Boolean leaf = false;
    /**
     * 多
     */
    private Boolean multi = false;
    /**
     * 最后的
     */
    private Boolean last = false;

    public FileTree(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    public FileTree() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public FileTree getParent() {
        return parent;
    }

    public void setParent(FileTree parent) {
        this.parent = parent;
    }

    public List<FileTree> getChildren() {
        return children;
    }

    public void setChildren(List<FileTree> children) {
        this.children = children;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Boolean getMulti() {
        return multi;
    }

    public void setMulti(Boolean multi) {
        this.multi = multi;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public void addChildren(FileTree fileTree) {
        if (CollectionUtils.isEmpty(getChildren())) {
            this.children = new ArrayList<>();
        }
        if (this.children.stream().noneMatch(fileTree1 -> fileTree1.getName().equals(fileTree.getName()))) {
            this.children.add(fileTree);
        }
    }

}
