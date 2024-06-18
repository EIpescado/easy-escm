package org.group1418.easy.escm.core.system.service;

import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.core.system.entity.SystemTenant;
import org.group1418.easy.escm.core.system.pojo.fo.SystemTenantFo;
import org.group1418.easy.escm.core.system.pojo.qo.SystemTenantQo;
import org.group1418.easy.escm.core.system.pojo.to.SystemTenantTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemTenantVo;

/**
 * 租户 服务类
 *
 * @author yq 2024-06-07
 */
public interface ISystemTenantService extends BaseService<SystemTenant> {

    /**
     * 校验租户
     *
     * @param tenantId 租户ID
     */
    void check(String tenantId);

    /**
     * 创建 租户
     *
     * @param fo 表单
     */
    void create(SystemTenantFo fo);

    /**
     * 更新 租户
     *
     * @param id 主键
     * @param fo 参数
     */
    void update(Long id, SystemTenantFo fo);

    /**
     * 详情
     *
     * @param id 主键
     * @return SystemTenantVo
     */
    SystemTenantVo get(Long id);

    /**
     * 根据租户编码获取租户详情
     *
     * @param tenantId 租户编码
     * @return SystemTenantVo
     */
    SystemTenantVo get(String tenantId);

    /**
     * 删除
     *
     * @param id 主键
     */
    void delete(Long id);

    /**
     * 列表
     *
     * @param qo 查询参数
     * @return 分页对象
     */
    PageR<SystemTenantTo> search(SystemTenantQo qo);
}
