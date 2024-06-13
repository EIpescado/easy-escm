package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.group1418.easy.escm.common.base.impl.BaseServiceImpl;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.group1418.easy.escm.common.utils.DbUtil;
import org.group1418.easy.escm.common.wrapper.ButtonNode;
import org.group1418.easy.escm.core.constant.CacheConstant;
import org.group1418.easy.escm.core.system.entity.SystemButton;
import org.group1418.easy.escm.core.system.mapper.SystemButtonMapper;
import org.group1418.easy.escm.core.system.pojo.fo.SystemButtonFo;
import org.group1418.easy.escm.core.system.service.ISystemButtonService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yq
 * @date 2020-09-26 11:35:17
 * @description 按钮
 * @since V1.0.0
 */
@Service
@RequiredArgsConstructor
public class SystemButtonServiceImpl extends BaseServiceImpl<SystemButtonMapper, SystemButton> implements ISystemButtonService {

    private final RedisCacheService redisCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SystemButtonFo fo) {
        SystemButton systemButton = new SystemButton();
        BeanUtils.copyProperties(fo, systemButton);
        baseMapper.insert(systemButton);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SystemButtonFo fo) {
        SystemButton systemButton = baseMapper.selectById(id);
        Assert.notNull(systemButton);
        BeanUtils.copyProperties(fo, systemButton);
        baseMapper.updateById(systemButton);
        DbUtil.afterTransactionCommit(() -> redisCacheService.hDel(CacheConstant.Hashs.SYSTEM_BUTTON,id.toString()));
    }

    @Override
    public List<ButtonNode> batchGetButtonNodes(List<String> buttonIds) {
        return baseMapper.batchGetButtonNodes(buttonIds);
    }

    @Override
    public List<ButtonNode> getAllButtonNodes() {
        return baseMapper.getAllButtonNodes();
    }

    @Override
    public List<String> getUserButtonPermission(Long userId) {
        return baseMapper.getUserButtonPermission(userId);
    }

    @Override
    public List<String> getAllButtonIds() {
        return baseMapper.getAllButtonIds();
    }

    @Override
    public List<String> getUserButtonIds(Long userId) {
        return baseMapper.getUserButtonIds(userId);
    }

}
