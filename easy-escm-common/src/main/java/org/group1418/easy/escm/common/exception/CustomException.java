package org.group1418.easy.escm.common.exception;

import lombok.Getter;
import org.group1418.easy.escm.common.enums.CustomTipEnum;
import org.group1418.easy.escm.common.enums.ICustomTipEnum;
import org.group1418.easy.escm.common.wrapper.CustomTip;

/**
 * 自定义异常
 *
 * @author yq
 * @date 2019/05/21 11:34
 */
@Getter
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = -4083494081772087464L;

    protected CustomTip tip;

    public CustomException(CustomTip customTip) {
        super(customTip.getMessage());
        this.tip = customTip;
    }

    public CustomException(ICustomTipEnum customTip) {
        super(customTip.tip().getMessage());
        this.tip = customTip.tip();
    }

    public CustomException(String msgI18nCode, Object... args) {
        this(new CustomTip(CustomTipEnum.FAIL.getCode(), msgI18nCode, args));
    }

    public CustomException(String message) {
        this(CustomTip.error(message));
    }
}
