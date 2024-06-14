package org.group1418.easy.escm.core.system.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.group1418.easy.escm.common.config.properties.EasyEscmConfig;
import org.group1418.easy.escm.common.constant.GlobalConstants;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.utils.PudgeUtil;
import org.group1418.easy.escm.common.utils.ValidateUtils;
import org.group1418.easy.escm.core.system.enums.LoginType;
import org.group1418.easy.escm.core.system.pojo.fo.LoginFo;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;
import org.group1418.easy.escm.core.system.service.IAuthService;
import org.group1418.easy.escm.core.system.service.ISystemClientService;
import org.group1418.easy.escm.core.system.service.ISystemTenantService;
import org.group1418.easy.escm.core.system.strategy.IAuthStrategy;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * AuthServiceImpl
 *
 * @author yq 2024/4/10 16:45
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final ISystemClientService systemClientService;
    private final ISystemTenantService systemTenantService;
    private final RedisCacheService redisCacheService;
    private final EasyEscmConfig easyEscmConfig;

    @Override
    public LoginVo login(String body) {
        LoginFo loginFo = JSON.parseObject(body, LoginFo.class);
        ValidateUtils.validate(loginFo);
        //客户端
        String clientId = loginFo.getClientId();
        //授权类型
        String grantType = loginFo.getGrantType();
        //租户
        Long tenantId = loginFo.getTenantId();
        SystemClientVo client = systemClientService.getByClientId(clientId);
        //客户端存在 且 授权类型包含
        if (client == null || !StrUtil.contains(client.getGrantType(), grantType)) {
            log.info("客户端[{}]授权类型[{}]不匹配", clientId, grantType);
            throw EasyEscmException.i18n("auth.client.not.exist.or.grant.type.not.match");
        }
        //客户端禁用
        if (AbleStateEnum.OFF == client.getState()) {
            log.info("客户端[{}]已被禁用", clientId);
            throw EasyEscmException.i18n("auth.client.disabled");
        }
        //校验租户
        systemTenantService.check(tenantId);
        return IAuthStrategy.login(body, client, grantType);
    }

    @Override
    public void logout() {
        try {
            StpUtil.logout();
        } catch (NotLoginException | EasyEscmException ignored) {
        }
    }

    @Override
    public void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        // 获取用户登录错误次数，默认为0 (可自定义限制策略 例如: key + username + ip)
        redisCacheService.getAndDel(GlobalConstants.PWD_ERR_CNT_KEY, username, (Integer en) -> {
            int errorNumber = PudgeUtil.null2Zero(en);
            //最大错误次数
            Integer maxRetryCount = easyEscmConfig.getLoginMaxRetryCount();
            //锁定时间
            Integer lockTime = easyEscmConfig.getLoginLockTime();
            // 锁定时间内登录 则踢出
            if (errorNumber >= maxRetryCount) {
                log.info("用户[{}]登录密码错误超出最大次数,当前[{}]", username, errorNumber);
                throw EasyEscmException.i18n(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            }
            if (supplier.get()) {
                // 错误次数+1
                errorNumber++;
                redisCacheService.incrementAndTtl(GlobalConstants.PWD_ERR_CNT_KEY, username, lockTime * 60, 1);
                // 达到规定错误次数 则锁定登录
                if (errorNumber >= maxRetryCount) {
                    log.info("用户[{}]登录密码错误超出最大次数,当前[{}]", username, errorNumber);
                    throw EasyEscmException.i18n(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
                } else {
                    // 未达到规定错误次数
                    log.info("用户[{}]登录密码错误,当前[{}]", username, errorNumber);
                    throw EasyEscmException.i18n(loginType.getRetryLimitCount(), errorNumber);
                }
            }
        });
    }

}
