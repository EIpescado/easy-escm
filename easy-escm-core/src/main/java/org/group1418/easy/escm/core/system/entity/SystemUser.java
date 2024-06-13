package org.group1418.easy.escm.core.system.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.group1418.easy.escm.common.base.obj.BaseEntity;
import org.group1418.easy.escm.common.base.obj.TenantEntity;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;

import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author yq
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SystemUser extends TenantEntity {

    private static final long serialVersionUID = 8411371755246793866L;
    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像文件ID
     */
    private Long avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 状态
     */
    private UserStateEnum state;

    /**
     * 激活时间
     */
    private LocalDateTime activateTime;
    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后登录ID
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

}
