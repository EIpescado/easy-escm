package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.base.impl.BaseServiceImpl;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.utils.DateTimeUtil;
import org.group1418.easy.escm.common.utils.PageUtil;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.core.constant.CacheConstant;
import org.group1418.easy.escm.core.system.entity.SystemTenant;
import org.group1418.easy.escm.core.system.mapper.SystemTenantMapper;
import org.group1418.easy.escm.core.system.pojo.fo.SystemTenantFo;
import org.group1418.easy.escm.core.system.pojo.qo.SystemTenantQo;
import org.group1418.easy.escm.core.system.pojo.to.SystemTenantTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemTenantVo;
import org.group1418.easy.escm.core.system.service.ISystemTenantService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 租户 服务实现类
 *
 * @author yq 2024-06-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemTenantServiceImpl extends BaseServiceImpl<SystemTenantMapper, SystemTenant> implements ISystemTenantService {

    private final RedisCacheService redisCacheService;

    @Override
    public void check(Long tenantId) {
        if (tenantId == null) {
            throw EasyEscmException.i18n("tenant.required");
        }
        //租户不存在
        SystemTenantVo tenant = get(tenantId);
        if (tenant == null) {
            throw EasyEscmException.i18n("tenant.invalid");
        }
        //租户被禁用
        if (AbleStateEnum.OFF == tenant.getState()) {
            throw EasyEscmException.i18n("tenant.disabled");
        }
        //租户是否在有效期
        if (!DateTimeUtil.timeValid(LocalDateTime.now(), null, tenant.getExpireTime())) {
            throw EasyEscmException.i18n("tenant.expired");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SystemTenantFo fo) {
        SystemTenant systemTenant = new SystemTenant();
        BeanUtils.copyProperties(fo, systemTenant);
        baseMapper.insert(systemTenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SystemTenantFo fo) {
        SystemTenant systemTenant = baseMapper.selectById(id);
        Assert.notNull(systemTenant);
        BeanUtils.copyProperties(fo, systemTenant);
        baseMapper.updateById(systemTenant);
    }

    @Override
    public SystemTenantVo get(Long id) {
        return redisCacheService.hashRound(CacheConstant.Hashs.SYSTEM_TENANT, Long.toString(id), () -> baseMapper.get(id), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public PageR<SystemTenantTo> search(SystemTenantQo qo) {
        return PageUtil.select(qo, baseMapper::search);
    }
}
