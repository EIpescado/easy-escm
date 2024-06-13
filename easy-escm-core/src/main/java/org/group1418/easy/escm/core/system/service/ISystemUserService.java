package org.group1418.easy.escm.core.system.service;



import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;
import org.group1418.easy.escm.common.saToken.obj.CurrentUser;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.core.system.entity.SystemUser;
import org.group1418.easy.escm.core.system.pojo.fo.SystemUserFo;
import org.group1418.easy.escm.core.system.pojo.to.SystemUserTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemUserVo;


/**
 * 用户service 2024年6月13日 14:15:29
 * @author yq
 */
public interface ISystemUserService extends BaseService<SystemUser> {

    /**
     * 根据帐号获取用户
     *
     * @param username 帐号
     * @return 用户
     */
    SystemUser getUserByUsername(String username);

    /**
     * 用户列表
     *
     * @param qo 查询对象
     * @return 分页结果
     */
    PageR<SystemUserTo> list(BasePageQo qo);

    /**
     * 创建用户
     *
     * @param fo 用户表单
     * @return 用户ID
     */
    SystemUser create(SystemUserFo fo);

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param fo 用户表单
     */
    void update(Long id, SystemUserFo fo);

    /**
     * 用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    SystemUserVo get(Long id);

    /**
     * 重置密码
     *
     * @param id 用户ID
     */
    void resetPassword(Long id);

    /**
     * 变更用户状态
     *
     * @param id    用户id
     * @param state 状态
     */
    void switchState(Long id, UserStateEnum state);

    /**
     * 构建当前用户对象
     * @param user 用户
     * @return 当前用户
     */
    CurrentUser buildCurrentUser(SystemUser user);

}
