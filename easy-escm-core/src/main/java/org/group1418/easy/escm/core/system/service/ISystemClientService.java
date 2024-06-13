package org.group1418.easy.escm.core.system.service;


import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.core.system.entity.SystemClient;
import org.group1418.easy.escm.core.system.pojo.fo.SystemClientFo;
import org.group1418.easy.escm.core.system.pojo.to.SystemClientTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;

/**
 * system client
 * @author yq 2021-04-22 15:09:29
 */
public interface ISystemClientService extends BaseService<SystemClient> {

    /**
     * 新增
     *
     * @param fo 参数
     */
    void create(SystemClientFo fo);

    /**
     * 更新
     *
     * @param id 主键
     * @param fo 参数
     */
    void update(Long id, SystemClientFo fo);

    /**
     * 列表
     *
     * @param qo 查询参数
     * @return 分页对象
     */
    PageR<SystemClientTo> list(BasePageQo qo);


    /**
     * 详情
     *
     * @param id 主键
     * @return SystemClientVo
     */
    SystemClientVo get(Long id);


    /**
     * 详情
     *
     * @param clientId 客户端ID
     * @return SystemClientVo
     */
    SystemClientVo getByClientId(String clientId);

}
