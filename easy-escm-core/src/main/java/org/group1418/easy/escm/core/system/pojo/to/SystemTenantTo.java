package org.group1418.easy.escm.core.system.pojo.to;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.BaseTo;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;

import java.time.LocalDateTime;

/**
 * 租户 To
 *
 * @author yq 2024-06-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemTenantTo extends BaseTo {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID
     */
    private Long packageId;

    /**
     * 租户状态
     */
    private AbleStateEnum state;

    /**
     * 租户备注
     */
    private String remark;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 用户数量（-1不限制）
     */
    private Long userLimit;
}
