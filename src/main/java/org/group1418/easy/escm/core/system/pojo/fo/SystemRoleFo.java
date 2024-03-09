package org.group1418.easy.escm.core.system.pojo.fo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author yq
 * @date 2020-09-21 14:45:55
 * @description 角色 Fo
 * @since V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SystemRoleFo implements Serializable {
    private static final long serialVersionUID = -2787152552603004097L;
    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 备注
     */
    private String remark;
}
