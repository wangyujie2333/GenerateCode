package ${package};

<#list imports as import>
import ${import};
</#list>

/**
* ${comment}数据访问对象接口
*
* @author ${author}
* @date ${date}
*/
public interface ${simpleName} {

${methodsStr}
}