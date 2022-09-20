<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${mapperNamePath}">

    <resultMap id="BaseResultMap" type="${entityNamePath}">
<#list fields as field>
    <#if field.id>
        <id column="${field.columnName}" jdbcType="${field.jdbcType}" property="${field.name}"${field.javaType}/>
    <#else>
        <result column="${field.columnName}" jdbcType="${field.jdbcType}" property="${field.name}"${field.javaType}/>
    </#if>
</#list>
    </resultMap>

    <sql id="base_column_list">
    <#list fields as field>
        ${field.columnName}<#if field_index!=fields?size-1>,</#if>
    </#list>
    </sql>

    <sql id="table_name">
        ${tableName}
    </sql>

${methodsStr}
</mapper>