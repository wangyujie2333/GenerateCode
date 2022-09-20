package com.idea.plugin.setting.template;


import java.util.List;

public class SqlTemplateVO {
    private List<SqlTemplate> sqlTemplates;

    public SqlTemplateVO() {
    }

    public List<SqlTemplate> getSqlTemplates() {
        return sqlTemplates;
    }

    public void setSqlTemplates(List<SqlTemplate> sqlTemplates) {
        this.sqlTemplates = sqlTemplates;
    }

    public static class SqlTemplate {
        private String procedureType;
        private Boolean isMerge;
        private String comment;
        private SqlStr mysql;
        private SqlStr oracle;

        public SqlTemplate() {
        }

        public String getProcedureType() {
            return procedureType;
        }

        public void setProcedureType(String procedureType) {
            this.procedureType = procedureType;
        }

        public Boolean getIsMerge() {
            return isMerge;
        }

        public void setIsMerge(Boolean isMerge) {
            this.isMerge = isMerge;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public SqlStr getMysql() {
            return mysql;
        }

        public void setMysql(SqlStr mysql) {
            this.mysql = mysql;
        }

        public SqlStr getOracle() {
            return oracle;
        }

        public void setOracle(SqlStr oracle) {
            this.oracle = oracle;
        }
    }

    public static class SqlStr {
        private String procedure;
        private String call;
        private String drop;

        public SqlStr() {
        }

        public String getProcedure() {
            return procedure;
        }

        public void setProcedure(String procedure) {
            this.procedure = procedure;
        }

        public String getCall() {
            return call;
        }

        public void setCall(String call) {
            this.call = call;
        }

        public String getDrop() {
            return drop;
        }

        public void setDrop(String drop) {
            this.drop = drop;
        }
    }
}
