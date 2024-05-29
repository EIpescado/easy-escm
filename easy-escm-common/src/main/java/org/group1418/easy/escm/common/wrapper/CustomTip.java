package org.group1418.easy.escm.common.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.group1418.easy.escm.common.enums.CustomTipEnum;
import org.group1418.easy.escm.common.utils.I18nUtil;

import java.io.Serializable;

/**
 * 自定义提示
 *
 * @author yq 2019/05/21 10:49
 */
@NoArgsConstructor
@Data
public class CustomTip implements Comparable<CustomTip>, Serializable {

    private static final long serialVersionUID = -5682436596474651717L;
    /**
     * 错误编码
     */
    private String code;

    /**
     * 错误的国际化代码
     */
    private String msgI18nCode;

    /**
     * 国家化后的消息
     */
    private String message;

    /**
     * 参数
     */
    private Object[] args;

    public CustomTip(String code, String msgI18nCode, Object... args) {
        this.code = code;
        this.msgI18nCode = msgI18nCode;
        this.args = args;
    }

    public CustomTip(int code, String msgI18nCode, Object... args) {
        this.code = Integer.toString(code);
        this.msgI18nCode = msgI18nCode;
        this.args = args;
    }

    public static CustomTip error(String msgCode) {
        return new CustomTip(CustomTipEnum.FAIL.getCode(), msgCode);
    }


    public String getMessage() {
        return I18nUtil.getMessage(msgI18nCode, args);
    }

    @Override
    public int compareTo(CustomTip o) {
        return o.getCode().compareTo(this.code);
    }
}
