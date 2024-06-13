package org.group1418.easy.escm.core.system.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.core.system.pojo.fo.LoginFo;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;
import org.group1418.easy.escm.core.system.strategy.IAuthStrategy;
import org.springframework.stereotype.Component;

/**
 * PasswordAuthStrategy 帐号密码认证策略
 *
 * @author yq 2024/6/7 16:01
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PasswordAuthStrategy implements IAuthStrategy {
    @Override
    public LoginVo login(String body, SystemClientVo client) {
        LoginFo.PasswordLoginFo loginFo = validate(body, LoginFo.PasswordLoginFo.class);
        //验证码相关校验

        return null;
    }
}
