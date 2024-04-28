package ${package.Controller};

<#list easyEscmConfig.controllerPackages as cp>
import ${cp};
</#list>
/**
 * ${table.comment!} controller
 * @author ${author}
 * @since ${date}
 */
<#list easyEscmConfig.controllerAnnotations as ca>
@${ca}
</#list>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

  private final ${table.serviceName} ${easyEscmConfig.serviceImplFieldName};

}

