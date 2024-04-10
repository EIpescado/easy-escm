package org.group1418.easy.escm.core.system.service;


import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.wrapper.MenuTreeNode;
import org.group1418.easy.escm.core.system.entity.SystemMenu;
import org.group1418.easy.escm.core.system.pojo.fo.SystemMenuFo;

import java.util.List;


/**
 * @author yq
 * @date 2020-09-21 15:33:47
 * @description 菜单
 * @since V1.0.0
 */
public interface ISystemMenuService extends BaseService<SystemMenu> {

    /**
     * 新增
     *
     * @param fo 参数
     */
    void create(SystemMenuFo fo);

    /**
     * 更新
     *
     * @param id 主键
     * @param fo 参数
     */
    void update(Long id, SystemMenuFo fo);

    /**
     * 当前用户的菜单树
     * @param rootId 根节点ID, 1 PC,2小程序
     * @return 菜单tree
     */
    List<MenuTreeNode> userMenuTree(Long rootId);

    /**
     * 指定用户菜单树
     * @param rootId  根节点ID, 1 PC,2小程序
     * @param userId  用户ID
     * @param superAdmin   是否超级管理员
     * @return 菜单tree
     */
    List<MenuTreeNode> userMenuTree(Long rootId,Long userId, Boolean superAdmin);

    /**
     * 完整菜单tree
     * @return 完整菜单tree
     */
    List<MenuTreeNode> wholeTree();

    /**
     * 删除菜单或按钮
     * @param nodes 节点
     */
    void delete(List<MenuTreeNode> nodes);
}
