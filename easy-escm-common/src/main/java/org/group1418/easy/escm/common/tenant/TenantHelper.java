package org.group1418.easy.escm.common.tenant;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.group1418.easy.escm.common.constant.GlobalConstants;
import org.group1418.easy.escm.common.spring.SpringContextHolder;

import java.util.function.Supplier;

/**
 * 租户工具
 *
 * @author yq 2024年6月17日 12:26:06
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantHelper {

    private static final ThreadLocal<String> TEMP_DYNAMIC_TENANT = new TransmittableThreadLocal<>();

    private static final RedisCacheService REDIS_CACHE_SERVICE = SpringContextHolder.getBean(RedisCacheService.class);

    /**
     * 开启忽略租户(开启后需手动调用 {@link #disableIgnore()} 关闭)
     */
    public static void enableIgnore() {
        InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
    }

    /**
     * 关闭忽略租户
     */
    public static void disableIgnore() {
        InterceptorIgnoreHelper.clearIgnoreStrategy();
    }

    /**
     * 在忽略租户中执行,mybatis
     *
     * @param handle 处理执行方法
     */
    public static void ignore(Runnable handle) {
        enableIgnore();
        try {
            handle.run();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略租户中执行 mybatis
     *
     * @param handle 处理执行方法
     */
    public static <T> T ignore(Supplier<T> handle) {
        enableIgnore();
        try {
            return handle.get();
        } finally {
            disableIgnore();
        }
    }

    public static void setDynamic(boolean isLogin, String tenantId) {
        setDynamic(isLogin, tenantId, false);
    }

    /**
     * 设置动态租户(一直有效 需要手动清理)
     * <p>
     * 如果为未登录状态下 那么只在当前线程内生效
     *
     * @param isLogin  是否登录
     * @param tenantId 租户id
     * @param global   是否全局生效
     */
    public static void setDynamic(boolean isLogin, String tenantId, boolean global) {
        if (!isLogin || !global) {
            TEMP_DYNAMIC_TENANT.set(tenantId);
            return;
        }
        REDIS_CACHE_SERVICE.set(GlobalConstants.Strings.DYNAMIC_TENANT_KEY, StpUtil.getTokenValue(), () -> tenantId, null);
    }

    /**
     * 获取动态租户(一直有效 需要手动清理)
     * <p>
     * 如果为未登录状态下 那么只在当前线程内生效
     */
    public static String getDynamic() {
        // 如果线程内有值 优先返回
        String tenantId = TEMP_DYNAMIC_TENANT.get();
        if (StrUtil.isNotBlank(tenantId)) {
            return tenantId;
        }
        String tokenValue = StpUtil.getTokenValue();
        // 全局动态租户
        tenantId = REDIS_CACHE_SERVICE.get(GlobalConstants.Strings.DYNAMIC_TENANT_KEY, tokenValue);
        if (StrUtil.isNotBlank(tenantId)) {
            return tenantId;
        }
        // token登录租户
        tenantId = REDIS_CACHE_SERVICE.get(GlobalConstants.Strings.TOKEN_TENANT, tokenValue);
        return tenantId;
    }

    /**
     * 清除动态租户
     *
     * @param isLogin 是否已登录
     */
    public static void clearDynamic(boolean isLogin) {
        if (!isLogin) {
            TEMP_DYNAMIC_TENANT.remove();
            return;
        }
        TEMP_DYNAMIC_TENANT.remove();
        REDIS_CACHE_SERVICE.del(GlobalConstants.Strings.DYNAMIC_TENANT_KEY, StpUtil.getTokenValue());
    }

    /**
     * 在动态租户中执行
     *
     * @param isLogin  是否登录
     * @param tenantId 租户ID
     * @param handle   处理执行方法
     */
    public static void dynamic(boolean isLogin, String tenantId, Runnable handle) {
        setDynamic(isLogin, tenantId);
        try {
            handle.run();
        } finally {
            clearDynamic(isLogin);
        }
    }

    /**
     * 在动态租户中执行
     *
     * @param isLogin  是否已登录
     * @param tenantId 租户编码
     * @param handle   处理执行方法
     */
    public static <T> T dynamic(boolean isLogin, String tenantId, Supplier<T> handle) {
        setDynamic(isLogin, tenantId);
        try {
            return handle.get();
        } finally {
            clearDynamic(isLogin);
        }
    }

    /**
     * 设置token对应的租户ID
     *
     * @param token    凭证
     * @param tenantId 租户ID
     * @param ttl      有效时间 单位秒
     */
    public static void setTokenTenant(String token, String tenantId, long ttl) {
        REDIS_CACHE_SERVICE.set(GlobalConstants.Strings.TOKEN_TENANT, token, ttl, () -> tenantId, null);
    }

    /**
     * 获取当前租户id(动态租户优先)
     */
    public static String getTenantId() {
        return TenantHelper.getDynamic();
    }

    /**
     * 获取当前凭证对应租户并放入线程上下文
     */
    public static void setLocal() {
        String tenantId = REDIS_CACHE_SERVICE.get(GlobalConstants.Strings.TOKEN_TENANT, StpUtil.getTokenValue());
        TEMP_DYNAMIC_TENANT.set(tenantId);
        log.info("set local tenant[{}]",tenantId);
    }

    /**
     * 移除当前凭证对应租户并放入线程上下文
     */
    public static void clearLocal() {
        TEMP_DYNAMIC_TENANT.remove();
        log.info("remove local tenant");
    }

}
