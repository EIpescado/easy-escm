package org.group1418.easy.escm.core.system.service;

import org.group1418.easy.escm.core.system.enums.LoginType;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;

import java.util.function.Supplier;

/**
 * IAuthService auth相关
 *
 * @author yq 2024/4/10 16:44
 */
public interface IAuthService {

    /**
     * 登录
     *
     * @param body 登录信息
     * @return 响应结果
     */
    LoginVo login(String body);


    /**
     * 登录校验
     *
     * @param loginType 登录类型
     * @param username  用户名
     * @param supplier  登录是否失败的额外判断
     */
    void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier);

}
