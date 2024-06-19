package org.group1418.easy.escm.common.interceptor;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.group1418.easy.escm.common.config.properties.EasyEscmTokenProp;
import org.group1418.easy.escm.common.tenant.TenantHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EasyEscmSaInterceptor
 * 自定义 SaInterceptor 添加租户支持,自动在线程上下文绑定租户,接口执行完成后移除
 * 所有非配置过滤和@SaIgnore标识的接口全部需登录
 * @author yq 2024/6/19 16:45
 */
public class EasyEscmSaInterceptor extends SaInterceptor {

    public EasyEscmSaInterceptor(EasyEscmTokenProp easyEscmTokenProp) {
        super(handle ->
                SaRouter.match("/**")
                        .notMatch(easyEscmTokenProp.getNotCheckLoginPaths())
                        .check(r -> {
                            //检查是否登录
                            StpUtil.checkLogin();
                        }));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TenantHelper.setLocal();
        return super.preHandle(request, response, handler);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantHelper.clearLocal();
    }
}
