package org.group1418.easy.escm.core.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.TenantEntity;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;

/**
 * @author yq
 * @date 2020/09/21 14:11
 * @description 角色
 * @since V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemRole extends TenantEntity {
    private static final long serialVersionUID = -3777984388888594077L;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 状态
     */
    private AbleStateEnum state;

    /**
     * 备注
     */
    private String remark;
}
