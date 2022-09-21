package ${package};

import java.io.Serializable;
<#list imports as import>
import ${import};
</#list>

/**
* ${comment}
*
* @author ${author}
* @date ${date}
*/
public class ${simpleName} implements Serializable {

    private static final long serialVersionUID = 1L;

<#list fields as field>
    /**
    * ${field.comment}
    */
    private ${field.classSimpleName} ${field.name};
</#list>

    public ${simpleName}() {
    }

<#list fields as field>
    public ${field.classSimpleName} get${field.name?cap_first}() {
        return ${field.name};
    }

    public void set${field.name?cap_first} (${field.classSimpleName} ${field.name}) {
        this.${field.name} = ${field.name};
    }

</#list>
}