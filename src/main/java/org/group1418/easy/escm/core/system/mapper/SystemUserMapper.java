package org.group1418.easy.escm.core.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.enums.OrgEnum;
import org.group1418.easy.escm.core.system.entity.SystemUser;
import org.group1418.easy.escm.core.system.pojo.to.SystemClientTo;
import org.group1418.easy.escm.core.system.pojo.to.SystemUserTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemUserVo;

import java.util.List;

/**
 * @author yq
 * @date 2020/09/21 11:49
 * @description 系统用户mapper
 * @since V1.0.0
 */
public interface SystemUserMapper extends CommonMapper<SystemUser> {

    /**
     * 分页查询对象
     *
     * @param page mybatis-plus分页参数
     * @param qo   查询对象
     * @return 分页结果
     */
    IPage<SystemUserTo> list(Page<SystemUserTo> page, @Param("qo") BasePageQo qo);

    /**
     * 用户详情
     *
     * @param id 用户ID
     * @return 详情
     */
    SystemUserVo get(@Param("id") Long id);

    /**
     * 获取指定用户绑定的组织
     * @param username 用户名
     * @return 组织
     */
    List<OrgEnum> getUserOrg(@Param("username") String username);

    /**
     * 获取指定用户绑定的组织
     * @param username 用户名
     * @return 组织
     */
    List<SystemClientTo> getUserOrgPro(@Param("username")String username);
}
