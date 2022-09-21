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
@RestController
@RequestMapping("/${moduleName}/${name?lower_case}")
public class ${simpleName} {

    @Autowired
    ${iserviceName} ${iserviceName?uncap_first};

${methodsStr}
}