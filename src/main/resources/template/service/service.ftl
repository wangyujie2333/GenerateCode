package ${package};

<#list imports as import>
import ${import};
</#list>

/**
* ${comment}服务层类
*
* @author ${author}
* @date ${date}
*/
@Service
public class ${simpleName} implements ${iserviceName} {

    @Autowired
    private ${mapperName} ${mapperName?uncap_first};

${methodsStr}
}