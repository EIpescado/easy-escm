package org.group1418.easy.escm.common.wrapper;


import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.group1418.easy.escm.common.enums.EasyEscmTipEnum;
import org.group1418.easy.escm.common.enums.IEasyEscmTipEnum;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.utils.I18nUtil;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 返回结果
 *
 * @author yq 2018年12月27日 16:03:56
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 7285612697346692975L;
    /**
     * 返回结果 编码 0：成功 1：失败
     */
    private String code;
    /**
     * 返回结果 描述信息
     */
    private String message;
    /**
     * 返回结果
     */
    private T res;

    public R() {
    }

    public R(String code, String message, T res) {
        this.code = code;
        this.message = message;
        this.res = res;
    }

    /**
     * 判断返回是否成功
     */
    public boolean succeed() {
        return code.equals(EasyEscmTipEnum.SUCCESS.getCode());
    }

    public static <T> R<T> ok(T res) {
        return new R<>(EasyEscmTipEnum.SUCCESS.getCode(), EasyEscmTipEnum.SUCCESS.getMsgI18nCode(), res);
    }

    public static R<String> ok() {
        return ok(StrUtil.EMPTY);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(EasyEscmTipEnum.FAIL.getCode(), message, null);
    }

    public static <T> R<T> fail(IEasyEscmTipEnum tip) {
        return new R<>(tip.getCode(), I18nUtil.getMessage(tip.getMsgI18nCode()), null);
    }

    public static <T> R<T> failI18n(String msgI18nCode, Object... args) {
        return new R<>(EasyEscmTipEnum.FAIL.getCode(), I18nUtil.getMessage(msgI18nCode, args), null);
    }

    public static <T> R<T> fail(Integer code, String msg) {
        return new R<>(code != null ? code.toString() : EasyEscmTipEnum.FAIL.getCode(), msg, null);
    }

    public static <T> R<T> fail(String code, String msg) {
        return new R<>(code, msg, null);
    }

    public void then(Consumer<T> okConsumer, VoidFunc0 catchFun) {
        if (succeed()) {
            okConsumer.accept(this.res);
        } else {
            catchFun.callWithRuntimeException();
        }
    }

    public void thenWithThrow(Consumer<T> okConsumer) {
        if (succeed()) {
            okConsumer.accept(this.res);
        } else {
            throw EasyEscmException.simple(this.message);
        }
    }

    public void then(Consumer<T> okConsumer, Consumer<String> errorMessageConsumer) {
        if (succeed()) {
            okConsumer.accept(this.res);
        } else {
            errorMessageConsumer.accept(this.message);
        }
    }

    public <BR> BR thenBackWithThrow(Function<T, BR> okFunction) {
        if (succeed()) {
            return okFunction.apply(this.res);
        } else {
            throw EasyEscmException.simple(this.message);
        }
    }
}
