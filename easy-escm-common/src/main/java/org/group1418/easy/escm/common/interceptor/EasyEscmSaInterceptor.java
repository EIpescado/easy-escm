package org.group1418.easy.escm.common.interceptor;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import org.group1418.easy.escm.common.config.properties.EasyEscmTokenProp;
import org.group1418.easy.escm.common.tenant.TenantHelper;
import org.group1418.easy.escm.common.utils.PudgeUtil;
import org.group1418.easy.escm.common.wrapper.R;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * EasyEscmSaInterceptor
 * 自定义 SaInterceptor 添加租户支持,自动在线程上下文绑定租户,接口执行完成后移除
 * 所有非配置过滤和@SaIgnore标识的接口全部需登录
 *
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
        try {
            // 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
            if (this.isAnnotation && handler instanceof HandlerMethod) {
                // 获取此请求对应的 Method 处理函数
                Method method = ((HandlerMethod) handler).getMethod();
                // 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权
                if (SaStrategy.instance.isAnnotationPresent.apply(method, SaIgnore.class)) {
                    // 注意这里直接就退出整个鉴权了，最底部的 auth.run() 路由拦截鉴权也被跳出了
                    return true;
                }
                // 注解校验
                SaStrategy.instance.checkMethodAnnotation.accept(method);
            }
            // Auth 校验
            this.auth.run(handler);
        } catch (StopMatchException stopMatchException) {
            // StopMatchException 异常代表：停止匹配，进入Controller
        } catch (BackResultException backResultException) {
            // BackResultException 异常代表：停止匹配，向前端输出结果
            PudgeUtil.responseJson(response, R.fail(backResultException.getMessage()));
            return false;
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantHelper.clearLocal();
    }
}
