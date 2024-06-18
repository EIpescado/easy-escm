package org.group1418.easy.escm.core.system.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.tenant.TenantHelper;
import org.group1418.easy.escm.common.utils.PudgeUtil;
import org.group1418.easy.escm.common.utils.ValidateUtils;
import org.group1418.easy.escm.core.system.entity.SystemUser;
import org.group1418.easy.escm.core.system.enums.LoginType;
import org.group1418.easy.escm.core.system.pojo.fo.LoginFo;
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
    public SystemUser login(String body, SystemClientVo client) {
        LoginFo.PasswordLoginFo loginFo = validate(body, LoginFo.PasswordLoginFo.class);
        ValidateUtils.validate(loginFo);
        //todo 验证码相关校验
        String username = loginFo.getUsername();
        String password = loginFo.getPassword();
        //动态插入登录的租户
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
        return user;
    }
}
