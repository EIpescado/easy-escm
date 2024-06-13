package org.group1418.easy.escm.common.exception;

import lombok.Getter;
import org.group1418.easy.escm.common.enums.EasyEscmTipEnum;
import org.group1418.easy.escm.common.enums.IEasyEscmTipEnum;
import org.group1418.easy.escm.common.wrapper.CustomTip;

/**
 * 自定义异常
 *
 * @author yq 2019/05/21 11:34
 */
@Getter
public class BaseEasyEscmException extends RuntimeException {
    private static final long serialVersionUID = -4083494081772087464L;

    protected CustomTip tip;

    public BaseEasyEscmException(CustomTip customTip) {
        super(customTip.getMessage());
        this.tip = customTip;
    }

    public BaseEasyEscmException(IEasyEscmTipEnum customTip) {
        super(customTip.tip().getMessage());
        this.tip = customTip.tip();
    }

    public BaseEasyEscmException(String msgI18nCode, Object... args) {
        this(new CustomTip(EasyEscmTipEnum.FAIL.getCode(), msgI18nCode, args));
    }

    public BaseEasyEscmException(String message) {
        this(CustomTip.error(message));
    }
}
