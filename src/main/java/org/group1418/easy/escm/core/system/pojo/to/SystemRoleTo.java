package org.group1418.easy.escm.core.system.pojo.to;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.group1418.easy.escm.common.base.obj.BaseTo;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yq
 * @date 2020-09-21 14:45:55
 * @description 角色 To
 * @since V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SystemRoleTo extends BaseTo {
    private static final long serialVersionUID = 616430700585411048L;
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
