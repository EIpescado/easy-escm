package org.group1418.easy.escm.core.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.wrapper.Selector;
import org.group1418.easy.escm.core.system.entity.SystemRole;
import org.group1418.easy.escm.core.system.pojo.to.SystemRoleTo;

import java.util.List;


/**
 * @author yq
 * @date 2020-09-21 14:45:55
 * @description 角色 Mapper
 * @since V1.0.0
 */
public interface SystemRoleMapper extends CommonMapper<SystemRole> {

    /**
     * 列表
     *
     * @param page mybatis-plus分页参数
     * @param qo   查询参数
     * @return 列表
     */
    IPage<SystemRoleTo> list(Page<SystemRoleTo> page, @Param("qo") BasePageQo qo);

    /**
     * 获取指定用户角色
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    List<String> getUserRole(@Param("userId") Long userId);

    /**
     * 角色集合
     *
     * @return 角色集合
     */
    List<Selector<Long>> select();

    /**
     * 获取角色ID集合
     * @param codeList 角色编码
     * @return 角色ID集合
     */
    List<Long> getRoleIdsByCode(@Param("codeList")List<String> codeList);
}
