package org.group1418.easy.escm.core.system.service;



import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.wrapper.ButtonNode;
import org.group1418.easy.escm.core.system.entity.SystemButton;
import org.group1418.easy.escm.core.system.pojo.fo.SystemButtonFo;

import java.util.List;


/**
 * @author yq
 * @date 2020-09-26 11:35:17
 * @description 按钮
 * @since V1.0.0
 */
public interface ISystemButtonService extends BaseService<SystemButton> {

    /**
     * 新增
     * @param fo 参数
     */
    void create(SystemButtonFo fo);

    /**
     * 更新
     * @param id 主键
     * @param fo 参数
     */
    void update(Long id, SystemButtonFo fo);

    /**
     * 指定id所有按钮
     * @param buttonIds 按钮ID集合
     * @return 按钮集合
     */
    List<ButtonNode> batchGetButtonNodes(List<String> buttonIds);

    /**
     * 系统所有按钮
     * @return 全部按钮
     */
    List<ButtonNode> getAllButtonNodes();

    /**
     * 获取用户所有按钮权限
     * @param userId 用户ID
     * @return 按钮权限集合
     */
    List<String> getUserButtonPermission(Long userId);

    /**
     * 系统所有按钮ID
     * @return 全部按钮ID
     */
    List<String> getAllButtonIds();

    /**
     * 获取用户所有按钮
     * @param userId 用户ID
     * @return ID集合
     */
    List<String> getUserButtonIds(Long userId);
}
