package org.group1418.easy.escm.common.saToken.obj;

import lombok.Data;

/**
 * CurrentUser
 * @author yq 2024/3/7 17:54
 */
@Data
public class CurrentUser {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 昵称
     */
    private String nickname;

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
     * 客户端ID
     */
    private String clientId;

    /**
     * 设备类型
     */
    private String deviceType;
}
