package org.group1418.easy.escm.common.exception;

/**
 * 系统自定义异常
 * @author yq 2020/09/21 12:04
 */
public class SystemCustomException extends CustomException {

    private static final long serialVersionUID = -6632920847550172761L;

    private SystemCustomException(String msgI18nCode, Object... args) {
        super(msgI18nCode, args);
    }

    private SystemCustomException(String message) {
        super(message);
    }

    public static SystemCustomException i18n(String msgI18nCode, Object... args) {
        return new SystemCustomException(msgI18nCode,args);
    }

    public static SystemCustomException simple(String message) {
        return new SystemCustomException(message);
    }
}
