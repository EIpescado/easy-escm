package org.group1418.easy.escm.core.system.service;

import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;

/**
 * @author yq 2024/4/10 16:44
 * @description IAuthService auth相关
 */
public interface IAuthService {

    /**
     * 登录
     * @param body 登录信息
     * @return 响应结果
     */
    LoginVo login(String body);
}
