package org.group1418.easy.escm.common.cache;

import cn.hutool.core.util.StrUtil;
import org.redisson.api.NameMapper;

/**
 * RedisKeyPrefixNameMapper
 * redis 缓存key前缀处理
 *
 * @author yq 2024/6/11 13:48
 */
public class RedisKeyPrefixNameMapper implements NameMapper {

    private final String prefix;

    public RedisKeyPrefixNameMapper(String prefix) {
        this.prefix = StrUtil.isBlank(prefix) ? StrUtil.EMPTY : prefix + StrUtil.COLON;
    }

    /**
     * 给原始key添加前缀,若已有前缀则不添加
     * @param name - original Redisson object name
     * @return 新key
     */
    @Override
    public String map(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        if (StrUtil.isNotBlank(prefix) && !name.startsWith(prefix)) {
            return prefix + name;
        }
        return name;
    }

    /**
     * 从key中获取原始key
     * @param name - mapped name
     * @return 原始key
     */
    @Override
    public String unmap(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        if (StrUtil.isNotBlank(prefix) && name.startsWith(prefix)) {
            return name.substring(prefix.length());
        }
        return name;
    }
}
