package org.group1418.easy.escm.common.enums;


import org.group1418.easy.escm.common.exception.ICustomTipEnum;
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
    CREDENTIALS_INVALID(10001, "token.invalid"),
    REFRESH_CREDENTIALS_INVALID(10002, "refresh.token.invalid"),
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
