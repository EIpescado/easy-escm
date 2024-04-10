package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.group1418.easy.escm.common.base.impl.BaseServiceImpl;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.service.CustomRedisCacheService;
import org.group1418.easy.escm.common.utils.PageUtil;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.core.CacheConstant;
import org.group1418.easy.escm.core.system.entity.SystemClient;
import org.group1418.easy.escm.core.system.mapper.SystemClientMapper;
import org.group1418.easy.escm.core.system.pojo.fo.SystemClientFo;
import org.group1418.easy.escm.core.system.pojo.to.SystemClientTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;
import org.group1418.easy.escm.core.system.service.ISystemClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yq
 * @date 2021-04-22 15:09:29
 * @description system client
 * @since V1.0.0
 */
@Service
@RequiredArgsConstructor
public class SystemClientServiceImpl extends BaseServiceImpl<SystemClientMapper, SystemClient> implements ISystemClientService {

    private final CustomRedisCacheService customRedisCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SystemClientFo fo) {
        SystemClient systemClient = new SystemClient();
        BeanUtils.copyProperties(fo, systemClient);
        baseMapper.insert(systemClient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SystemClientFo fo) {
        SystemClient systemClient = baseMapper.selectById(id);
        Assert.notNull(systemClient);
        BeanUtils.copyProperties(fo, systemClient);
        baseMapper.updateById(systemClient);
    }

    @Override
    public PageR<SystemClientTo> list(BasePageQo qo) {
        return PageUtil.select(qo, baseMapper::list);
    }

    @Override
    public SystemClientVo get(Long id) {
        return baseMapper.get(id);
    }

    @Override
    public SystemClientVo getByClientId(String clientId) {
        return customRedisCacheService.hashRound(CacheConstant.Hashs.SYSTEM_CLIENT, clientId, () -> baseMapper.getByClientId(clientId), null);
    }

}
