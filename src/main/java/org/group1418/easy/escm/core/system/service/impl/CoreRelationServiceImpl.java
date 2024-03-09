package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.enums.OrgEnum;
import org.group1418.easy.escm.core.system.mapper.CoreRelationMapper;
import org.group1418.easy.escm.core.system.service.ICoreRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yq
 * @date 2020/09/22 10:03
 * @description 关系service
 * @since V1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreRelationServiceImpl implements ICoreRelationService {

    private final CoreRelationMapper relationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userBindRoles(Long userId, List<Long> roleIds) {
        if (userId != null) {
            //先删除所有已有角色
            relationMapper.deleteUserRoleRelationByUserId(userId);
            if (CollectionUtil.isNotEmpty(roleIds)) {
                //绑定新角色
                relationMapper.batchInsertUserRoleRelation(userId, roleIds);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userAddRoles(Long userId, List<Long> roleIds) {
        if (userId != null) {
            if (CollectionUtil.isNotEmpty(roleIds)) {
                //过滤已经绑定的关系
                List<Long> alreadyBindRoleIds = this.getUserAlreadyBindRoleIds(userId);
                if (CollUtil.isNotEmpty(alreadyBindRoleIds)) {
                    roleIds.removeAll(alreadyBindRoleIds);
                }
                if(CollUtil.isNotEmpty(roleIds)){
                    //绑定新角色
                    relationMapper.batchInsertUserRoleRelation(userId, roleIds);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userRemoveRoles(Long userId, List<Long> roleIds) {
        if (userId != null) {
            if (CollectionUtil.isNotEmpty(roleIds)) {
                relationMapper.deleteUserRoleRelationByUserIdAndRoleIdIn(userId, roleIds);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void roleBindMenus(Long roleId, List<Long> menuIds) {
        if (roleId != null) {
            //先删除所有已绑定菜单
            relationMapper.deleteRoleMenuRelationByRoleId(roleId);
            if (CollectionUtil.isNotEmpty(menuIds)) {
                //绑定新菜单
                relationMapper.batchInsertRoleMenuRelation(roleId, menuIds);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void roleBindButtons(Long roleId, List<Long> buttonIds) {
        if (roleId != null) {
            //先删除所有已绑定按钮
            relationMapper.deleteRoleButtonRelationByRoleId(roleId);
            if (CollectionUtil.isNotEmpty(buttonIds)) {
                //绑定新按钮
                relationMapper.batchInsertRoleButtonRelation(roleId, buttonIds);
            }
        }
    }

    @Override
    public List<Long> singleRoleMenu(Long roleId) {
        return relationMapper.singleRoleMenu(roleId);
    }

    @Override
    public List<Long> singleRoleButton(Long roleId) {
        return relationMapper.singleRoleButton(roleId);
    }

    @Override
    public List<Long> getUserAlreadyBindRoleIds(Long userId) {
        return relationMapper.getUserAlreadyBindRoleIds(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userBindTenant(Long userId, Long tenantId, OrgEnum organization) {
        if (userId != null && tenantId != null) {
            //用户绑定租户
            Integer userBindRows = relationMapper.countByUserIdAndTenantId(userId, tenantId);
            if (userBindRows == null || userBindRows == 0) {
                log.info("用户[{}]绑定租户[{}]", userId, tenantId);
                relationMapper.bindUserTenantRelation(userId, tenantId);
            }
            //租户绑定组织
            Integer bindRows = relationMapper.countByTenantIdAndOrganization(tenantId, organization);
            //未绑定
            if (bindRows == null || bindRows == 0) {
                log.info("租户[{}]绑定组织[{}]", tenantId, organization);
                relationMapper.bindTenantOrganizationRelation(tenantId, organization);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void tenantBindOrganization(Long tenantId, OrgEnum organization) {
        //租户绑定组织
        Integer bindRows = relationMapper.countByTenantIdAndOrganization(tenantId, organization);
        //未绑定
        if (bindRows == null || bindRows == 0) {
            log.info("租户[{}]绑定组织[{}]", tenantId, organization);
            relationMapper.bindTenantOrganizationRelation(tenantId, organization);
        }
    }

}
