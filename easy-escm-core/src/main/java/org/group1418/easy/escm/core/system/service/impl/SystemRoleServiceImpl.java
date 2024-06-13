package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.group1418.easy.escm.common.base.impl.BaseServiceImpl;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.cache.CustomRedisCacheService;
import org.group1418.easy.escm.common.utils.PageUtil;
import org.group1418.easy.escm.common.wrapper.MenuTreeNode;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.common.wrapper.Selector;
import org.group1418.easy.escm.core.system.entity.SystemRole;
import org.group1418.easy.escm.core.system.mapper.SystemRoleMapper;
import org.group1418.easy.escm.core.system.pojo.fo.SystemRoleFo;
import org.group1418.easy.escm.core.system.pojo.to.SystemRoleTo;
import org.group1418.easy.escm.core.system.service.ICoreRelationService;
import org.group1418.easy.escm.core.system.service.ISystemButtonService;
import org.group1418.easy.escm.core.system.service.ISystemMenuService;
import org.group1418.easy.escm.core.system.service.ISystemRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yq
 * @date 2020-09-21 14:45:55
 * @description 角色
 * @since V1.0.0
 */
@Service
@RequiredArgsConstructor
public class SystemRoleServiceImpl extends BaseServiceImpl<SystemRoleMapper, SystemRole> implements ISystemRoleService {

    private final ISystemMenuService menuService;
    private final ISystemButtonService buttonService;
    private final ICoreRelationService coreRelationService;
    private final CustomRedisCacheService customRedisCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SystemRoleFo fo) {
        SystemRole systemRole = new SystemRole();
        BeanUtils.copyProperties(fo, systemRole);
        baseMapper.insert(systemRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SystemRoleFo fo) {
        SystemRole systemRole = baseMapper.selectById(id);
        Assert.notNull(systemRole);
        BeanUtils.copyProperties(fo, systemRole, "name");
        baseMapper.updateById(systemRole);
    }

    @Override
    public PageR<SystemRoleTo> list(BasePageQo qo) {
        return PageUtil.select(qo, baseMapper::list);
    }

    @Override
    public List<String> getUserRole(Long userId) {
        return baseMapper.getUserRole(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindMenuAndButton(Long roleId, List<MenuTreeNode> nodes) {
        //删除角色所有已绑定节点
        if (CollectionUtil.isNotEmpty(nodes)) {
            Map<Boolean, List<MenuTreeNode>> map = nodes.stream().collect(Collectors.groupingBy(MenuTreeNode::getBeButton));
            //绑定菜单
            List<MenuTreeNode> menus = map.get(Boolean.FALSE);
            if (CollectionUtil.isNotEmpty(menus)) {
                coreRelationService.roleBindMenus(roleId, menus.stream().map(MenuTreeNode::getId).collect(Collectors.toList()));
            }
            //绑定按钮
            List<MenuTreeNode> buttons = map.get(Boolean.TRUE);
            if (CollectionUtil.isNotEmpty(buttons)) {
                coreRelationService.roleBindButtons(roleId, buttons.stream().map(MenuTreeNode::getId).collect(Collectors.toList()));
            }
        }
    }

    @Override
    public List<String> singleRoleMenuAndButton(Long roleId) {
        List<Long> menuIds = coreRelationService.singleRoleMenu(roleId);
        List<Long> buttonIds = coreRelationService.singleRoleButton(roleId);
        if (CollectionUtil.isNotEmpty(menuIds)) {
            if (CollectionUtil.isNotEmpty(buttonIds)) {
                menuIds.addAll(buttonIds);
            }
            //fastJson 即使配置全局对Long的序列化 ToStringSerializer , 序列化 List<Long> 使用的 ListSerializer 未对Long做处理 依旧返回Long
            return menuIds.stream().map(Object::toString).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<Selector<Long>> select() {
        return baseMapper.select();
    }

    @Override
    public List<Long> getRoleIdsByCode(List<String> codeList) {
        return baseMapper.getRoleIdsByCode(codeList);
    }

}
