package org.group1418.easy.escm.core.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.core.system.entity.SystemClient;
import org.group1418.easy.escm.core.system.pojo.to.SystemClientTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;

/**
 * @author yq
 * @date 2021-04-22 15:09:29
 * @description system client Mapper
 * @since V1.0.0
 */
public interface SystemClientMapper extends CommonMapper<SystemClient> {

    /**
     * 列表
     * @param page mybatis-plus分页参数
     * @param qo 查询参数
     * @return 列表
     */
    IPage<SystemClientTo> list(Page<SystemClientTo> page, @Param("qo") BasePageQo qo);

    /**
    * 详情
    * @param id ID
    * @return 详情
    */
    SystemClientVo get(@Param("id") Long id);

    /**
     * 详情
     * @param clientId 客户端ID
     * @return 详情
     */
    SystemClientVo getByClientId(@Param("clientId")String clientId);
}
