package org.group1418.easy.escm.core.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.group1418.easy.escm.common.wrapper.ButtonNode;
import org.group1418.easy.escm.core.system.entity.SystemButton;

import java.util.List;

/**
 * @author yq
 * @date 2020-09-26 11:35:17
 * @description 按钮 Mapper
 * @since V1.0.0
 */
public interface SystemButtonMapper extends CommonMapper<SystemButton> {


    /**
     * 获取指定所有按钮node
     * @param buttonIds 按钮ID集合
     * @return buttons
     */
    List<ButtonNode> batchGetButtonNodes(@Param("buttonIds") List<String> buttonIds);

    /**
     * 所有按钮
     * @return 按钮集合
     */
    List<ButtonNode> getAllButtonNodes();

    /**
     * 获取用户按钮权限
     * @param userId 用户ID
     * @return 按钮权限
     */
    List<String> getUserButtonPermission(@Param("userId") Long userId);

    /**
     * 所有按钮ID集合
     * @return 按钮ID
     */
    List<String> getAllButtonIds();

    /**
     * 用户按钮ID集合
     * @param userId 用户ID
     * @return 按钮ID集合
     */
    List<String> getUserButtonIds(@Param("userId")Long userId);
}
