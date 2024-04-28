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
    <#if easyEscmConfig.fo && table.commonFields?? && (table.commonFields?size > 0)>
    @PostMapping
    public R<#noparse><</#noparse>String<#noparse>></#noparse> create(@RequestBody ${entity}Fo fo){
        ${easyEscmConfig.serviceImplFieldName}.create(fo);
        return R.ok();
    }

    @PostMapping("{id}")
    public R<#noparse><</#noparse>String<#noparse>></#noparse> update(@PathVariable(name = "id")${table.commonFields[0].propertyType} id, @RequestBody ${entity}Fo fo){
        ${easyEscmConfig.serviceImplFieldName}.update(id,fo);
        return R.ok();
    }
    </#if>

    <#if easyEscmConfig.vo && table.commonFields?? && (table.commonFields?size > 0)>
    @GetMapping("{id}")
    public R<#noparse><</#noparse>${entity}Vo<#noparse>></#noparse> get(@PathVariable(name = "id")${table.commonFields[0].propertyType} id){
        return R.ok(${easyEscmConfig.serviceImplFieldName}.get(id));
    }
    </#if>

    <#if table.commonFields?? && (table.commonFields?size > 0)>
    @PostMapping("/delete/{id}")
    public R<#noparse><</#noparse>String<#noparse>></#noparse> delete(@PathVariable(name = "id")${table.commonFields[0].propertyType} id){
        ${easyEscmConfig.serviceImplFieldName}.delete(id);
        return R.ok();
    }
    </#if>

    <#if easyEscmConfig.qo>
    @GetMapping
    public R<#noparse><PageR</#noparse><#noparse><</#noparse>${entity}To<#noparse>>></#noparse> list(@ModelAttribute ${entity}Qo qo){
        return R.ok(${easyEscmConfig.serviceImplFieldName}.list(qo));
    }
    </#if>
}