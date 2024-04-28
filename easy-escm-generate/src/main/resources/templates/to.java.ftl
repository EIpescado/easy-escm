package ${easyEscmConfig.pojoPackage}.to;

<#list easyEscmConfig.pojoPackages as pkg>
import ${pkg};
</#list>
import lombok.Data;
import lombok.EqualsAndHashCode;
import ${easyEscmConfig.superToClassPackage};
/**
* ${table.comment!} To
* @author ${author}
* @since ${date}
*/
@EqualsAndHashCode(callSuper = true)
@Data
<#if easyEscmConfig.superToName??>
public class ${entity}To extends ${easyEscmConfig.superToName} {
<#else>
public class ${entity}To {
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
