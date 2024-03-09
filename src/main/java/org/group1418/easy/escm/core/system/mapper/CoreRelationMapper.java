package org.group1418.easy.escm.core.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.group1418.easy.escm.common.enums.OrgEnum;

import java.util.List;

/**
 * @author yq
 * @date 2020/09/22 10:04
 * @description 关系
 * @since V1.0.0
 */
public interface CoreRelationMapper {

    /**
     * 批量插入用户角色关系
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     * @return 影响行数
     */
    Integer batchInsertUserRoleRelation(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 删除指定用户所有绑定的角色
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    Integer deleteUserRoleRelationByUserId(@Param("userId") Long userId);

    /**
     * 删除指定用户指定角色
     *
     * @param userId  用户
     * @param roleIds 角色ID集合
     * @return 影响行
     */
    Integer deleteUserRoleRelationByUserIdAndRoleIdIn(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 删除指定角色已绑定菜单
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    Integer deleteRoleMenuRelationByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关系
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID集合
     * @return 影响行数
     */
    Integer batchInsertRoleMenuRelation(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    /**
     * 删除角色已绑定的按钮
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    Integer deleteRoleButtonRelationByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色按钮关系
     *
     * @param roleId    角色ID
     * @param buttonIds 按钮ID集合
     * @return 影响行数
     */
    Integer batchInsertRoleButtonRelation(@Param("roleId") Long roleId, @Param("buttonIds") List<Long> buttonIds);

    /**
     * 单个角色菜单
     *
     * @param roleId 角色ID
     * @return 菜单ID集合
     */
    List<Long> singleRoleMenu(@Param("roleId") Long roleId);

    /**
     * 单个角色按钮
     *
     * @param roleId 角色ID
     * @return 按钮集合
     */
    List<Long> singleRoleButton(@Param("roleId") Long roleId);

    /**
     * 获取用户已经绑定的所有角色ID
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    List<Long> getUserAlreadyBindRoleIds(@Param("userId") Long userId);

    /**
     * 指定用户是否已经与租户绑定关系
     *
     * @param userId     用户ID
     * @param tenantId   租户ID
     * @return 已绑定记录数
     */
    Integer countByUserIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    /**
     * 用户绑定租户
     *
     * @param userId     用户ID
     * @param tenantId   租户ID
     * @return 影响行
     */
    Integer bindUserTenantRelation(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    /**
     * 指定租户是否已绑定组织
     *
     * @param tenantId   租户ID
     * @param organization 组织
     * @return 已绑定记录数
     */
    Integer countByTenantIdAndOrganization(@Param("tenantId") Long tenantId, @Param("organization") OrgEnum organization);

    /**
     * 租户绑定组织
     *
     * @param tenantId   租户ID
     * @param organization 组织
     * @return 银行行数
     */
    Integer bindTenantOrganizationRelation(@Param("tenantId") Long tenantId, @Param("organization") OrgEnum organization);

}
