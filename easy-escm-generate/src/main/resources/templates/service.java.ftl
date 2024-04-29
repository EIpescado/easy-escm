package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};
<#list easyEscmConfig.servicePackages as cp>
import ${cp};
</#list>
/**
 * ${table.comment!} 服务类
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {
    <#if easyEscmConfig.fo && table.commonFields?? && (table.commonFields?size > 0)>

    /**
    * 创建 ${table.comment!}
    * @param fo 表单
    */
    void create(${entity}Fo fo);

    /**
    * 更新 ${table.comment!}
    * @param id 主键
    * @param fo 参数
    */
    void update(${table.commonFields[0].propertyType} id, ${entity}Fo fo);
    </#if>
    <#if easyEscmConfig.vo && table.commonFields?? && (table.commonFields?size > 0)>

    /**
    * 详情
    * @param id 主键
    * @return ${entity}Vo
    */
    ${entity}Vo get(${table.commonFields[0].propertyType} id);
    </#if>

    <#if easyEscmConfig.del && table.commonFields?? && (table.commonFields?size > 0)>
    /**
    * 删除
    * @param id 主键
    */
    void delete(${table.commonFields[0].propertyType} id);
    </#if>

    <#if easyEscmConfig.qo>
    /**
    * 列表
    * @param qo 查询参数
    * @return 分页对象
    */
    PageR<#noparse><</#noparse>${entity}To<#noparse>></#noparse> list(${entity}Qo qo);
    </#if>
}
</#if>
