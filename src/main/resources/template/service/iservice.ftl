package ${package};

<#list imports as import>
import ${import};
</#list>

/**
* ${comment}服务接口类
*
* @author ${author}
* @date ${date}
*/
public interface ${simpleName} {

${methodsStr}
}