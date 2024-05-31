package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.base.impl.BaseServiceImpl;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.config.properties.CustomConfigProperties;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;
import org.group1418.easy.escm.common.exception.SystemCustomException;
import org.group1418.easy.escm.common.utils.PageUtil;
import org.group1418.easy.escm.common.utils.PudgeUtil;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.core.system.entity.SystemUser;
import org.group1418.easy.escm.core.system.mapper.SystemUserMapper;
import org.group1418.easy.escm.core.system.pojo.fo.SystemUserFo;
import org.group1418.easy.escm.core.system.pojo.to.SystemUserTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemUserVo;
import org.group1418.easy.escm.core.system.service.ICoreRelationService;
import org.group1418.easy.escm.core.system.service.ISystemUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author yq 2024/3/4 15:51
 * @description SystemUserServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUserMapper, SystemUser> implements ISystemUserService {

    private final CustomConfigProperties customConfigProperties;
    private final ICoreRelationService coreRelationService;

    @Override
    public SystemUser getUserByUsername(String username) {
        return super.getOneByFieldValueEq(SystemUser::getUsername, username);
    }

    @Override
    public PageR<SystemUserTo> list(BasePageQo qo) {
        return PageUtil.select(qo, baseMapper::list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemUser create(SystemUserFo fo) {
        //判断用户名是否存在
        if (super.haveFieldValueEq(SystemUser::getUsername, fo.getUsername())) {
            //用户名已被使用
            throw SystemCustomException.i18n("user.username.used");
        }
        log.info("新增用户: [{}]", fo.getUsername());
        SystemUser user = new SystemUser();
        BeanUtils.copyProperties(fo, user);
        //给予初始密码
        user.setPassword(PudgeUtil.encodePwd(customConfigProperties.getUserDefaultPassword()));
        //正常
        user.setState(UserStateEnum.NORMAL);
        baseMapper.insert(user);
        //绑定角色
        coreRelationService.userBindRoles(user.getId(), fo.getRoleIds());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SystemUserFo fo) {
        SystemUser user = baseMapper.selectById(id);
        Assert.notNull(user);
        SystemUser oldUser = super.getOneByFieldValueEq(SystemUser::getUsername, fo.getUsername());
        //判断用户名是否存在
        if (oldUser != null && !oldUser.getId().equals(id)) {
            //用户名已被使用
            throw SystemCustomException.i18n("user.username.used");
        }
        log.info("修改用户: [{}]", user.getUsername());
        BeanUtils.copyProperties(fo, user);
        baseMapper.updateById(user);
        //绑定角色
        coreRelationService.userBindRoles(user.getId(), fo.getRoleIds());
    }

    @Override
    public SystemUserVo get(Long id) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        SystemUser user = baseMapper.selectById(id);
        Assert.notNull(user);
        this.update(Wrappers.<SystemUser>lambdaUpdate()
                .set(SystemUser::getPassword, getDefaultPassword())
                .set(SystemUser::getUpdateTime, LocalDateTime.now())
                .eq(SystemUser::getId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchState(Long id, UserStateEnum state) {
        SystemUser user = baseMapper.selectById(id);
        Assert.notNull(user);
        if (state.equals(user.getState())) {
            return;
        }
        log.info("用户[{}]变更状态,从[{}]->[{}]}", id, user.getState(), state);
        this.update(Wrappers.<SystemUser>lambdaUpdate()
                .set(SystemUser::getState, state)
                .set(SystemUser::getUpdateTime, LocalDateTime.now())
                .eq(SystemUser::getId, id));
    }

    private String getDefaultPassword() {
        return PudgeUtil.encodePwd(customConfigProperties.getUserDefaultPassword());
    }
}
