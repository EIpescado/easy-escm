package org.group1418.easy.escm.common.cache;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.constant.GlobalConstants;
import org.redisson.api.NameMapper;

/**
 * TenantRedisKeyPrefixNameMapper
 * redis 缓存key前缀处理
 *
 * @author yq 2024/6/11 13:48
 */
@Slf4j
public class TenantRedisKeyPrefixNameMapper extends RedisKeyPrefixNameMapper {


    public TenantRedisKeyPrefixNameMapper(String prefix) {
        super(prefix);
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
        if (InterceptorIgnoreHelper.willIgnoreTenantLine("")) {
            return super.map(name);
        }
        if (StrUtil.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.map(name);
        }
//        String tenantId = TenantHelper.getTenantId();
        String tenantId = "";
        if (StrUtil.isBlank(tenantId)) {
            log.error("无法获取有效的租户id -> Null");
        }
        if (StrUtil.isNotBlank(tenantId) && StrUtil.startWith(name, tenantId)) {
            // 如果存在则直接返回
            return super.map(name);
        }
        return super.map(StrUtil.isNotBlank(tenantId) ? tenantId + ":" + name : name);
    }

    /**
     * 从key中获取原始key
     * @param name - mapped name
     * @return 原始key
     */
    @Override
    public String unmap(String name) {
        String unmap = super.unmap(name);
        if (StrUtil.isBlank(unmap)) {
            return null;
        }
        if (InterceptorIgnoreHelper.willIgnoreTenantLine("")) {
            return super.unmap(name);
        }
        if (StrUtil.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.unmap(name);
        }
//        String tenantId = TenantHelper.getTenantId();
        String tenantId = "";
        if (StrUtil.isBlank(tenantId)) {
            log.error("无法获取有效的租户id -> Null");
        }
        if (StrUtil.isNotBlank(tenantId) && StrUtil.startWith(unmap, tenantId)) {
            // 如果存在则删除
            return unmap.substring((tenantId + ":").length());
        }
        return unmap;
    }
}
