package org.group1418.easy.escm.core.system.pojo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yq 2024/4/10 14:53
 * @description LoginVo 登录后返回
 */
@Data
public class LoginVo implements Serializable {
    private static final long serialVersionUID = 1659517212665409977L;

    /**
     * 授权令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 授权令牌 accessToken 的有效期
     */
    private Long expireIn;

    /**
     * 刷新令牌 refreshToken 的有效期
     */
    private Long refreshExpireIn;

    /**
     * 应用id
     */
    private String clientId;

    /**
     * 令牌权限
     */
    private String scope;

    /**
     * 用户 openid
     */
    private String openid;
}
