package com.idea.plugin.sql.support;


public class IndexInfoVO {
    public String indexName;
    public String indexColumnName;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexColumnName() {
        return indexColumnName;
    }

    public void setIndexColumnName(String indexColumnName) {
        this.indexColumnName = indexColumnName;
    }

    public static IndexInfoVO builder() {
        return new IndexInfoVO();
    }

    public IndexInfoVO indexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public IndexInfoVO indexColumnName(String indexColumnName) {
        this.indexColumnName = indexColumnName;
        return this;
    }
}
