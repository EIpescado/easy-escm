package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
<#if generateService>
import ${package.Service}.${table.serviceName};
</#if>
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<#list easyEscmConfig.serviceImplPackages as cp>
import ${cp};
</#list>
/**
 * ${table.comment!} 服务实现类
 * @author ${author} ${date}
 */
@Service
@RequiredArgsConstructor
@Slf4j
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>()<#if generateService>, ${table.serviceName}</#if> {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}><#if generateService> implements ${table.serviceName}</#if> {
    <#if easyEscmConfig.fo && table.commonFields?? && (table.commonFields?size > 0)>

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(${entity}Fo fo) {
        ${entity} ${entity?uncap_first} = new ${entity}();
        BeanUtils.copyProperties(fo,${entity?uncap_first});
        baseMapper.insert(${entity?uncap_first});
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(${table.commonFields[0].propertyType} id, ${entity}Fo fo) {
        ${entity} ${entity?uncap_first} = baseMapper.selectById(id);
        Assert.notNull(${entity?uncap_first});
        BeanUtils.copyProperties(fo,${entity?uncap_first});
        baseMapper.updateById(${entity?uncap_first});
    }
    </#if>
    <#if easyEscmConfig.vo && table.commonFields?? && (table.commonFields?size > 0)>

    @Override
    public ${entity}Vo get(${table.commonFields[0].propertyType} id) {
        return baseMapper.get(id);
    }
    </#if>

    <#if easyEscmConfig.del && table.commonFields?? && (table.commonFields?size > 0)>
    @Override
    @Transactional(rollbackFor = Exception.class)
        public void delete(${table.commonFields[0].propertyType} id){
        baseMapper.deleteById(id);
    }
    </#if>

    <#if easyEscmConfig.qo>
    @Override
    public PageR<#noparse><</#noparse>${entity}To<#noparse>></#noparse> search(${entity}Qo qo) {
        return PageUtil.select(qo, baseMapper::search);
    }
    </#if>
}
</#if>