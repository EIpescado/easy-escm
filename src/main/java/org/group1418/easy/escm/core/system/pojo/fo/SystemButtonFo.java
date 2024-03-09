package org.group1418.easy.escm.core.system.pojo.fo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

/**
 * @author yq
 * @date 2020-09-26 11:35:17
 * @description 按钮 Fo
 * @since V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SystemButtonFo implements Serializable {
    private static final long serialVersionUID = 5529749010150803094L;
    @NotBlank(message = "按钮名称必填")
    private String buttonName;
    @NotNull(message = "按钮所属菜单必填")
    private Long menuId;
    @Positive(message = "按钮排序号必须大于0")
    private Integer sortNo;
    private String icon;
    @NotBlank(message = "按钮位置必填")
    private String position;
    @NotBlank(message = "按钮绑定函数必填")
    private String click;
    private String permission;
}
