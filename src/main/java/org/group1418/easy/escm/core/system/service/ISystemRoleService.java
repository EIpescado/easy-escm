package org.group1418.easy.escm.core.system.service;


import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.wrapper.MenuTreeNode;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.common.wrapper.Selector;
import org.group1418.easy.escm.core.system.entity.SystemRole;
import org.group1418.easy.escm.core.system.pojo.fo.SystemRoleFo;
import org.group1418.easy.escm.core.system.pojo.to.SystemRoleTo;

import java.util.List;


/**
 * @author yq
 * @date 2020-09-21 14:45:55
 * @description 角色
 * @since V1.0.0
 */
public interface ISystemRoleService extends BaseService<SystemRole> {

    /**
     * 新增
     *
     * @param fo 参数
     */
    void create(SystemRoleFo fo);

    /**
     * 更新
     *
     * @param id 主键
     * @param fo 参数
     */
    void update(Long id, SystemRoleFo fo);

    /**
     * 列表
     *
     * @param qo 查询参数
     * @return 分页对象
     */
    PageR<SystemRoleTo> list(BasePageQo qo);

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return 角色代码集合
     */
    List<String> getUserRole(Long userId);

    /**
     * 角色绑定菜单和按钮
     *
     * @param roleId 角色ID
     * @param nodes  节点集合
     */
    void bindMenuAndButton(Long roleId, List<MenuTreeNode> nodes);

    /**
     * 单个角色已经绑定的菜单和按钮
     *
     * @param roleId 角色ID
     * @return 菜单和按钮ID集合
     */
    List<String> singleRoleMenuAndButton(Long roleId);

    /**
     * 角色下拉框
     *
     * @return 角色集合
     */
    List<Selector<Long>> select();

    /**
     * 根据编码获取角色ID
     *
     * @param codeList 编码
     * @return 角色ID集合
     */
    List<Long> getRoleIdsByCode(List<String> codeList);
}
