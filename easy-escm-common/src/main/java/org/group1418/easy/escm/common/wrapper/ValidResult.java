package org.group1418.easy.escm.common.wrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.Data;
import org.group1418.easy.escm.common.utils.I18nUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * ValidResult 校验结果
 *
 * @author yq 2021/4/27 16:36
 */
@Data
public class ValidResult implements Serializable {

    private static final long serialVersionUID = 7792117038622168318L;
    /**
     * 字段名称
     */
    private String fieldName;
    private boolean pass;
    private String msgI18nCode;
    private String message;

    public ValidResult(String fieldName) {
        this.fieldName = fieldName;
    }

    public ValidResult of(boolean pass, String msgI18nCode, Object... args) {
        this.msgI18nCode = msgI18nCode;
        this.pass = pass;
        //不通过才获取提示信息
        if (!pass) {
            this.message = I18nUtil.getMessage(msgI18nCode, ArrayUtil.insert(args, 0, fieldName));
        }
        return this;
    }

    public ValidResult pass(Object... args) {
        return of(true, null, args);
    }

    public ValidResult no(String msgI18nCode, Object... args) {
        return of(false, msgI18nCode, args);
    }

}
