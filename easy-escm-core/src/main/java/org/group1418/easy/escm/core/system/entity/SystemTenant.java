package org.group1418.easy.escm.core.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.group1418.easy.escm.common.base.obj.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 租户
 * @author yq 2024-06-07
 */
@Getter
@Setter
@TableName("system_tenant")
public class SystemTenant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编码
     */
    @TableField("code")
    private String code;

    /**
     * 套餐ID
     */
    @TableField("package_id")
    private Long packageId;

    /**
     * 租户状态
     */
    @TableField("state")
    private String state;

    /**
     * 租户备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 用户数量（-1不限制）
     */
    @TableField("user_limit")
    private Long userLimit;
}
