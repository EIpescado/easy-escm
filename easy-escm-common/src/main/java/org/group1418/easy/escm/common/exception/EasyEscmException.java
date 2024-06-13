package org.group1418.easy.escm.common.exception;

/**
 * 系统自定义异常
 * @author yq 2020/09/21 12:04
 */
public class EasyEscmException extends BaseEasyEscmException {

    private static final long serialVersionUID = -6632920847550172761L;

    private EasyEscmException(String msgI18nCode, Object... args) {
        super(msgI18nCode, args);
    }

    private EasyEscmException(String message) {
        super(message);
    }

    public static EasyEscmException i18n(String msgI18nCode, Object... args) {
        return new EasyEscmException(msgI18nCode,args);
    }

    public static EasyEscmException simple(String message) {
        return new EasyEscmException(message);
    }
}
