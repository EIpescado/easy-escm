package org.group1418.easy.escm.common.enums;


import cn.hutool.core.util.StrUtil;

import java.util.Arrays;

/**
 * 枚举实现接口,方便excel序列化
 *
 * @author yq 2024/5/30 17:34
 */
public interface IBaseEnum {

    String name();

    default IBaseEnum getMatch(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        return null;
//        return Arrays.stream(values())
//                .filter(b -> StrUtil.equalsAnyIgnoreCase(key, b.toString(), b.name()))
//                .findFirst().orElse(null);
    }

}
