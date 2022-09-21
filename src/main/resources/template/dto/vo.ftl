package ${package};

<#list imports as import>
import ${import};
</#list>

/**
* ${comment}
*
* @author ${author}
* @date ${date}
*/
public class ${simpleName} extends ${entityName} {

    private static final long serialVersionUID = 1L;

    public ${simpleName}() {
    }

}