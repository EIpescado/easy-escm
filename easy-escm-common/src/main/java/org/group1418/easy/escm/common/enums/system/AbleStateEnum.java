package org.group1418.easy.escm.common.enums.system;

import org.group1418.easy.escm.common.enums.IBaseEnum;

/**
 * 状态枚举, 启用禁用
 *
 * @author yq 2021年9月24日 10:10:19
 */
public enum AbleStateEnum implements IBaseEnum {
    /**
     * 状态
     */
    ON("启用"),
    OFF("禁用"),
    ;

    final String state;

    AbleStateEnum(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }

//    @Override
//    public Class<?> getEnumClass() {
//        return AbleStateEnum.class;
//    }
}
