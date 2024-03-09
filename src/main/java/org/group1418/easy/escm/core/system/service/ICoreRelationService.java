package org.group1418.easy.escm.core.system.service;


import org.group1418.easy.escm.common.enums.OrgEnum;

import java.util.List;

/**
 * @author yq
 * @date 2020/09/22 09:59
 * @description 关系绑定
 * @since V1.0.0
 */
public interface ICoreRelationService {

    /**
     * 用户绑定角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     */
    void userBindRoles(Long userId, List<Long> roleIds);

    /**
     * 用户添加角色,即不删除旧有角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     */
    void userAddRoles(Long userId, List<Long> roleIds);

    /**
     * 用户解绑角色,即不删除旧有角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     */
    void userRemoveRoles(Long userId, List<Long> roleIds);

    /**
     * 角色绑定菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID集合
     */
    void roleBindMenus(Long roleId, List<Long> menuIds);

    /**
     * 角色绑定按钮
     *
     * @param roleId    角色ID
     * @param buttonIds 按钮ID集合
     */
    void roleBindButtons(Long roleId, List<Long> buttonIds);

    /**
     * 单个角色所有菜单
     *
     * @param roleId 角色ID
     * @return 所有菜单
     */
    List<Long> singleRoleMenu(Long roleId);

    /**
     * 单个角色所有按钮
     *
     * @param roleId 角色ID
     * @return 所有按钮
     */
    List<Long> singleRoleButton(Long roleId);

    /**
     * 获取用户已经绑定的角色
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    List<Long> getUserAlreadyBindRoleIds(Long userId);

    /**
     * 用户绑定租户, 租户绑定组织,租户可能在多组织间反复横跳
     *
     * @param userId       用户ID
     * @param tenantId     租户ID
     * @param organization 组织
     */
    void userBindTenant(Long userId, Long tenantId, OrgEnum organization);

    /**
     * 租户绑定组织
     *
     * @param tenantId     租户ID
     * @param organization 组织
     */
    void tenantBindOrganization(Long tenantId, OrgEnum organization);

}
