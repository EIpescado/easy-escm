package org.group1418.easy.escm.core.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.group1418.easy.escm.common.wrapper.MenuTreeNode;
import org.group1418.easy.escm.core.system.entity.SystemMenu;

import java.util.List;

/**
 * @author yq
 * @date 2020-09-21 15:33:47
 * @description 菜单 Mapper
 * @since V1.0.0
 */
public interface SystemMenuMapper extends CommonMapper<SystemMenu> {

    /**
     * 获取用户菜单权限
     *
     * @param userId 用户ID
     * @return 菜单权限
     */
    List<String> getUserMenuPermission(@Param("userId") Long userId);

    /**
     * 获取用户所有菜单TreeNode
     *
     * @param userId 用户ID
     * @return nodes
     */
    List<MenuTreeNode> getUserMenuTreeNodes(@Param("userId") Long userId);

    /**
     * 所有菜单
     *
     * @return 菜单集合
     */
    List<MenuTreeNode> getAllMenuTreeNodes();

    /**
     * 获取所有菜单和按钮
     *
     * @return 菜单和按钮集合
     */
    List<MenuTreeNode> getAllNodes();

    /**
     * 获取用户所有菜单的ID
     *
     * @param userId 用户ID集合
     * @return 菜单ID集合
     */
    List<String> getUserMenuIds(@Param("userId") Long userId);

    /**
     * 获取所有菜单ID
     *
     * @return 菜单ID集合
     */
    List<String> getAllMenuIds();
}
