package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;
import org.group1418.easy.escm.common.exception.SystemCustomException;
import org.group1418.easy.escm.common.utils.ValidateUtils;
import org.group1418.easy.escm.core.system.pojo.fo.LoginFo;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;
import org.group1418.easy.escm.core.system.service.IAuthService;
import org.group1418.easy.escm.core.system.service.ISystemClientService;
import org.springframework.stereotype.Service;

/**
 * @author yq 2024/4/10 16:45
 * @description AuthServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final ISystemClientService systemClientService;

    @Override
    public LoginVo login(String body) {
        LoginFo loginFo = JSON.parseObject(body, LoginFo.class);
        ValidateUtils.validate(loginFo);
        //客户端
        String clientId = loginFo.getClientId();
        //授权类型
        String grantType = loginFo.getGrantType();
        SystemClientVo client = systemClientService.getByClientId(clientId);
        //客户端存在 且 授权类型包含
        if(client == null || !StrUtil.contains(client.getGrantType(),grantType)){
            log.info("客户端[{}]授权类型[{}]不匹配", clientId, grantType);
            throw new SystemCustomException("客户端不存在或授权类型不匹配");
        }
        //客户端禁用
        if(AbleStateEnum.OFF == client.getState()){
            log.info("客户端[{}]已被禁用", clientId);
            throw new SystemCustomException("客户端已禁用");
        }
        //校验租户
        return null;
    }
}
