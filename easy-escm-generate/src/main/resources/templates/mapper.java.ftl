package ${package.Mapper};

import ${package.Entity}.${entity};
import ${superMapperClassPackage};
<#if mapperAnnotationClass??>
import ${mapperAnnotationClass.name};
</#if>
<#list easyEscmConfig.mapperPackages as cp>
import ${cp};
</#list>
/**
 * ${table.comment!} Mapper 接口
 * @author ${author} ${date}
 */
<#if mapperAnnotationClass??>
@${mapperAnnotationClass.simpleName}
</#if>
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {
    <#if easyEscmConfig.qo>
    /**
    * 列表
    * @param page 分页参数
    * @param qo 查询参数
    * @return 列表
    */
    IPage<#noparse><</#noparse>${entity}To<#noparse>></#noparse> search(Page<#noparse><</#noparse>${entity}To<#noparse>></#noparse> page, @Param("qo") ${entity}Qo qo);
    </#if>

    <#if easyEscmConfig.vo && table.commonFields?? && (table.commonFields?size > 0)>
    /**
    * 详情
    * @param id ID
    * @return ${entity}Vo
    */
    ${entity}Vo get(@Param("id")${table.commonFields[0].propertyType} id);
    </#if>
}
</#if>
