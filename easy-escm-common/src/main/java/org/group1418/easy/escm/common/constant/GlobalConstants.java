package org.group1418.easy.escm.common.constant;

/**
 * GlobalConstants 全局常量(业务无关的)
 *
 * @author yq 2024/6/11 18:33
 */
public interface GlobalConstants {

    /**
     * 全局redis key
     */
    String GLOBAL_REDIS_KEY = "global:";


    interface Strings {
        /**
         * 动态租户
         */
        String DYNAMIC_TENANT_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "dynamic_tenant";
        /**
         * token对应的租户
         */
        String TOKEN_TENANT = GlobalConstants.GLOBAL_REDIS_KEY + "token_tenant";
    }

    interface Hashs {
        /**
         * 系统客户端
         */
        String SYSTEM_CLIENT = GLOBAL_REDIS_KEY + "system_client";

        /**
         * 系统菜单
         */
        String SYSTEM_MENU = GLOBAL_REDIS_KEY + "system_menu";

        /**
         * 系统按钮
         */
        String SYSTEM_BUTTON = GLOBAL_REDIS_KEY + "system_button";

        /**
         * 租户
         */
        String SYSTEM_TENANT = GLOBAL_REDIS_KEY + "system_tenant";
    }

    interface Lists {
    }


    /**
     * 验证码 redis key
     */
    String CAPTCHA_CODE_KEY = GLOBAL_REDIS_KEY + "captcha_codes";

    /**
     * 防重提交 redis key
     */
    String REPEAT_SUBMIT_KEY = GLOBAL_REDIS_KEY + "repeat_submit";

    /**
     * 限流 redis key
     */
    String RATE_LIMIT_KEY = GLOBAL_REDIS_KEY + "rate_limit";

    /**
     * 登录账户密码错误次数 redis key
     */
    String PWD_ERR_CNT_KEY = GLOBAL_REDIS_KEY + "pwd_err_cnt";

    /**
     * 三方认证 redis key
     */
    String SOCIAL_AUTH_CODE_KEY = GLOBAL_REDIS_KEY + "social_auth_codes";
}
