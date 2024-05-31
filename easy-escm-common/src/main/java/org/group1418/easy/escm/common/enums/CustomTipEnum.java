package org.group1418.easy.escm.common.enums;


import org.group1418.easy.escm.common.wrapper.CustomTip;

/**
 * 提示枚举
 *
 * @author yq 2021年4月13日 16:48:24
 */
public enum CustomTipEnum implements ICustomTipEnum {
    /**
     * 通用异常
     */
    SUCCESS(0, "success"),
    FAIL(1, "fail"),
    TOKEN_INVALID(10001, "token.invalid"),
    TOKEN_TIMEOUT(10005, "token.timeout"),
    BE_REPLACED(10010, "token.be.replaced"),
    KICK_OUT(10015, "token.kick.out"),
    TOKEN_FREEZE(10020, "token.freeze"),
    TOKEN_PREFIX_ERROR(10025, "token.prefix.error"),
    REFRESH_TOKEN_INVALID(10002, "refresh.token.invalid"),
    PERMISSION_DENIED(401, "permission.denied"),

    /**
     * 服务器异常
     */
    NOT_FOUND(404, "service.not.found="),
    METHOD_NOT_ALLOWED(405, "service.method.not.allowed"),
    UNSUPPORTED_MEDIA_TYPE(415, "service.unsupported.media.type"),
    SERVER_ERROR(500, "service.server.error"),
    ;

    final CustomTip tip;

    CustomTipEnum(int code, String msgI18nCode) {
        this.tip = new CustomTip(code, msgI18nCode);
    }

    @Override
    public CustomTip tip() {
        return tip;
    }

}
