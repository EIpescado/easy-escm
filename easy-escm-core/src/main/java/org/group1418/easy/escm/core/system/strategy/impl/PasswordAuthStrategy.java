package org.group1418.easy.escm.core.system.strategy.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.saToken.CurrentUserHelper;
import org.group1418.easy.escm.common.saToken.obj.CurrentUser;
import org.group1418.easy.escm.common.utils.PudgeUtil;
import org.group1418.easy.escm.common.utils.ValidateUtils;
import org.group1418.easy.escm.core.system.entity.SystemUser;
import org.group1418.easy.escm.core.system.enums.LoginType;
import org.group1418.easy.escm.core.system.pojo.fo.LoginFo;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;
import org.group1418.easy.escm.core.system.service.IAuthService;
import org.group1418.easy.escm.core.system.service.ISystemUserService;
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

    private final ISystemUserService systemUserService;
    private final IAuthService authService;

    @Override
    public LoginVo login(String body, SystemClientVo client) {
        LoginFo.PasswordLoginFo loginFo = validate(body, LoginFo.PasswordLoginFo.class);
        ValidateUtils.validate(loginFo);
        //todo 验证码相关校验
        String username = loginFo.getUsername();
        String password = loginFo.getPassword();
        SystemUser user = systemUserService.getUserByUsername(username);
        if (user == null) {
            log.info("用户[{}]不存在", username);
            throw EasyEscmException.i18n("user.not.exists");
        }
        if (UserStateEnum.FORBIDDEN == user.getState()) {
            log.info("用户[{}]已被禁用", username);
            throw EasyEscmException.i18n("user.disabled");
        }
        //密码不一致则累加登录失败错误次数,限制登录
        authService.checkLogin(LoginType.PASSWORD, username, () -> !PudgeUtil.encodePwd(password).equals(user.getPassword()));
        CurrentUser currentUser = systemUserService.buildCurrentUser(user);
        currentUser.setClientId(client.getClientId());
        currentUser.setDeviceType(client.getDeviceType());

        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        // 生成token
        CurrentUserHelper.login(currentUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        return loginVo;
    }
}
