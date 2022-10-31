package com.idea.plugin.setting.template;


public class JavaTemplateVO {
    private String ormType;
    private Boolean lombok;
    private OrmTemplateVO mybatis;
    private OrmTemplateVO jpa;

    public JavaTemplateVO() {
    }

    public String getOrmType() {
        return ormType;
    }

    public void setOrmType(String ormType) {
        this.ormType = ormType;
    }

    public Boolean getLombok() {
        return lombok;
    }

    public void setLombok(Boolean lombok) {
        this.lombok = lombok;
    }

    public OrmTemplateVO getMybatis() {
        return mybatis;
    }

    public void setMybatis(OrmTemplateVO mybatis) {
        this.mybatis = mybatis;
    }

    public OrmTemplateVO getJpa() {
        return jpa;
    }

    public void setJpa(OrmTemplateVO jpa) {
        this.jpa = jpa;
    }

    public static enum OrmType {
        mybatis, jpa
    }

    public static class OrmTemplateVO {
        private String DO;
        private String VO;
        private String DAO;
        private String MAPPER;
        private String MAPPER_MYSQL;
        private String MAPPER_ORACLE;
        private String ISERVICE;
        private String SERVICE;
        private String CONTROLLER;

        public OrmTemplateVO() {
        }

        public String getDO() {
            return DO;
        }

        public void setDO(String DO) {
            this.DO = DO;
        }

        public String getVO() {
            return VO;
        }

        public void setVO(String VO) {
            this.VO = VO;
        }

        public String getDAO() {
            return DAO;
        }

        public void setDAO(String DAO) {
            this.DAO = DAO;
        }

        public String getMAPPER() {
            return MAPPER;
        }

        public void setMAPPER(String MAPPER) {
            this.MAPPER = MAPPER;
        }

        public String getMAPPER_MYSQL() {
            return MAPPER_MYSQL;
        }

        public void setMAPPER_MYSQL(String MAPPER_MYSQL) {
            this.MAPPER_MYSQL = MAPPER_MYSQL;
        }

        public String getMAPPER_ORACLE() {
            return MAPPER_ORACLE;
        }

        public void setMAPPER_ORACLE(String MAPPER_ORACLE) {
            this.MAPPER_ORACLE = MAPPER_ORACLE;
        }

        public String getISERVICE() {
            return ISERVICE;
        }

        public void setISERVICE(String ISERVICE) {
            this.ISERVICE = ISERVICE;
        }

        public String getSERVICE() {
            return SERVICE;
        }

        public void setSERVICE(String SERVICE) {
            this.SERVICE = SERVICE;
        }

        public String getCONTROLLER() {
            return CONTROLLER;
        }

        public void setCONTROLLER(String CONTROLLER) {
            this.CONTROLLER = CONTROLLER;
        }
    }

    public static boolean isJpa(JavaTemplateVO javaTemplateVO) {
        return javaTemplateVO != null && JavaTemplateVO.OrmType.jpa.name().equals(javaTemplateVO.getOrmType());
    }

}
