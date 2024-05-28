package ${easyEscmConfig.pojoPackage}.qo;

<#list easyEscmConfig.pojoPackages as pkg>
import ${pkg};
</#list>
import lombok.Data;
import lombok.EqualsAndHashCode;
import ${easyEscmConfig.superQoClassPackage};
/**
* ${table.comment!} Qo
* @author ${author}  ${date}
*/
@EqualsAndHashCode(callSuper = true)
@Data
<#if easyEscmConfig.superQoName??>
public class ${entity}Qo extends ${easyEscmConfig.superQoName} {
<#else>
public class ${entity}Qo {
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
