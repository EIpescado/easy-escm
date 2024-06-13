package org.group1418.easy.escm.core.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录类型
 *
 * @author yq 2024年6月13日 14:21:57
 */
@Getter
@AllArgsConstructor
public enum LoginType {

    /**
     * 密码登录
     */
    PASSWORD("user.password.retry.limit.exceed", "user.password.retry.limit.count"),

    /**
     * 短信登录
     */
    SMS("sms.code.retry.limit.exceed", "sms.code.retry.limit.count"),

    /**
     * 邮箱登录
     */
    MAIL("mail.code.retry.limit.exceed", "mail.code.retry.limit.count"),

    /**
     * 微信登录
     */
    WECHAT("", "");

    /**
     * 登录重试超出限制提示
     */
    final String retryLimitExceed;

    /**
     * 登录重试限制计数提示
     */
    final String retryLimitCount;
}
