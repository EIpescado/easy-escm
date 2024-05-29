package org.group1418.easy.escm.common.exception;


import org.group1418.easy.escm.common.wrapper.CustomTip;

/**
 * 提示枚举接口
 *
 * @author yq
 * @date 2019/01/30 16:53
 */
public interface ICustomTipEnum {

    /**
     * 提示
     *
     * @return tip
     */
    CustomTip tip();

    /**
     * 响应码
     *
     * @return 响应码
     */
    default String getCode() {
        return tip().getCode();
    }

    /**
     * 提示信息 I18N编码
     *
     * @return I18N编码
     */
    default String getMsgI18nCode() {
        return tip().getMsgI18nCode();
    }

}
