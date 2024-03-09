package org.group1418.easy.escm.core.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.BaseEntity;

/**
 * @author yq
 * @date 2020/09/26 11:22
 * @description 按钮
 * @since V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemButton extends BaseEntity {
    private static final long serialVersionUID = 8235439689257212003L;

    /**
     * 按钮名称
     */
    private String buttonName;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 图标
     */
    private String icon;
    /**
     * 按钮位置
     */
    private String position;
    /**
     * 点击触发的函数名称
     */
    private String click;
    /**
     * 权限标识
     */
    private String permission;

    /**
     * 排序号,用于按钮排序
     */
    private Integer sortNo;
}
