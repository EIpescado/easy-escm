package org.group1418.easy.escm.common.saToken;


import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.saToken.obj.CurrentUser;

import java.util.function.Supplier;

/**
 * 当前登录用户上下文
 *
 * @author yq 2024年3月7日 17:38:34
 */
public class CurrentUserHelper {

    public static final String USER_INFO_KEY = "userInfo";
    public static final String TENANT_ID_KEY = "tenantId";
    public static final String USER_ID_KEY = "userId";
    public static final String CLIENT_ID_KEY = "clientId";
    public static final String TENANT_ADMIN_KEY = "beTenantAdmin";
    public static final String SUPER_ADMIN_KEY = "beSuperAdmin";

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param currentUser 登录用户信息
     * @param model       配置参数
     */
    public static void login(CurrentUser currentUser, SaLoginModel model) {
        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        StpUtil.login(currentUser.getId(), model);
        //用户信息
        StpUtil.getTokenSession().set(USER_INFO_KEY, currentUser);
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    public static CurrentUser currentUser() {
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            throw EasyEscmException.i18n("token.invalid");
        }
        return (CurrentUser) session.get(USER_INFO_KEY);
    }

    /**
     * 根据token获取对应用户信息
     *
     * @param token token
     * @return 用户信息
     */
    public static CurrentUser currentUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            throw EasyEscmException.i18n("token.invalid");
        }
        return (CurrentUser) session.get(USER_INFO_KEY);
    }

    /**
     * 获取当前登录用户id
     */
    public static Long id() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取当前登录用户租户id
     */
    public static Long getTenantId() {
        return currentUser().getTenantId();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStorageIfAbsentSet(String key, Supplier<T> handle) {
        try {
            SaStorage storage = SaHolder.getStorage();
            Object obj = storage.get(key);
            if (ObjectUtil.isNull(obj)) {
                obj = handle.get();
                storage.set(key, obj);
            }
            return (T) obj;
        } catch (Exception e) {
            return null;
        }
    }
}
