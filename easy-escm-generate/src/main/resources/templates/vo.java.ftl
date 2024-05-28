package ${easyEscmConfig.pojoPackage}.vo;

<#list easyEscmConfig.pojoPackages as pkg>
import ${pkg};
</#list>
import lombok.Data;
import lombok.EqualsAndHashCode;
import ${easyEscmConfig.superFoClassPackage};
/**
* ${table.comment!} Vo
* @author ${author} ${date}
*/
@EqualsAndHashCode(callSuper = true)
@Data
<#if easyEscmConfig.superFoName??>
public class ${entity}Vo extends ${easyEscmConfig.superFoName} {
<#else>
public class ${entity}Vo {
</#if>
<#if entitySerialVersionUID>

    private static final long serialVersionUID = 1L;
</#if>
<#list table.fields as field>

<#if field.comment!?length gt 0>
<#if springdoc>
    @Schema(description = "${field.comment}")
<#elseif swagger>
    @ApiModelProperty("${field.comment}")
<#else>
    /**
    * ${field.comment}
    */
</#if>
</#if>
    private ${field.propertyType} ${field.propertyName};
</#list>
}
