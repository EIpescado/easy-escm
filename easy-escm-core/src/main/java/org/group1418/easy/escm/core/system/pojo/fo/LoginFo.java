package org.group1418.easy.escm.core.system.pojo.fo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.validator.annotation.StrCheck;

/**
 * 登录表单
 *
 * @author yq 2024/3/29 15:32
 */
@Data
public class LoginFo {
    /**
     * 客户端ID
     */
    @StrCheck(name = "客户端")
    private String clientId;
    /**
     * 授权类型
     */
    @StrCheck(name = "授权类型")
    private String grantType;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识, 验证码id
     */
    private String uuid;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PasswordLoginFo extends LoginFo{

        /**
         * 用户名
         */
        private String username;
        /**
         * 密码
         */
        private String password;
    }

}
