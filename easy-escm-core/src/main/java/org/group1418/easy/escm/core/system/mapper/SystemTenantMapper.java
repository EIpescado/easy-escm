package org.group1418.easy.escm.core.system.mapper;

import org.group1418.easy.escm.core.system.entity.SystemTenant;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.group1418.easy.escm.core.system.pojo.qo.SystemTenantQo;
import org.group1418.easy.escm.core.system.pojo.to.SystemTenantTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemTenantVo;
/**
 * 租户 Mapper 接口
 * @author yq 2024-06-07
 */
@Mapper
public interface SystemTenantMapper extends CommonMapper<SystemTenant> {
    /**
    * 列表
    * @param page 分页参数
    * @param qo 查询参数
    * @return 列表
    */
    IPage<SystemTenantTo> search(Page<SystemTenantTo> page, @Param("qo") SystemTenantQo qo);

    /**
    * 详情
    * @param id ID
    * @return SystemTenantVo
    */
    SystemTenantVo get(@Param("id")Long id);
}
