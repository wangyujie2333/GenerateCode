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
public interface ${simpleName} {

${methodsStr}
}