package org.group1418.easy.escm.core.system.strategy;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.saToken.CurrentUserHelper;
import org.group1418.easy.escm.common.saToken.obj.CurrentUser;
import org.group1418.easy.escm.common.spring.SpringContextHolder;
import org.group1418.easy.escm.common.utils.ValidateUtils;
import org.group1418.easy.escm.core.system.entity.SystemUser;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;
import org.group1418.easy.escm.core.system.service.ISystemUserService;

/**
 * 授权策略
 *
 * @author yq 2024年6月7日 15:47:07
 */
public interface IAuthStrategy {

    String BASE_NAME = "AuthStrategy";

    /**
     * 登录
     *
     * @param body      登录对象
     * @param client    授权管理视图对象
     * @param grantType 授权类型
     * @return 登录验证信息
     */
    static LoginVo login(String body, SystemClientVo client, String grantType) {
        // 授权类型和客户端id
        String beanName = grantType + BASE_NAME;
        if (!SpringContextHolder.containsBean(beanName)) {
            throw EasyEscmException.i18n("auth.grant.type.not.support");
        }
        IAuthStrategy instance = SpringContextHolder.getBean(beanName, IAuthStrategy.class);
        ISystemUserService systemUserService = SpringContextHolder.getBean(ISystemUserService.class);
        //登录返回user
        SystemUser user = instance.login(body, client);
        //构建登录后响应
        CurrentUser currentUser = systemUserService.buildCurrentUser(user);
        currentUser.setClientId(client.getClientId());
        currentUser.setDeviceType(client.getDeviceType());

        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        //自定义token过期时间 和 token 最低活跃频率
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        // 生成token
        CurrentUserHelper.login(currentUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        return loginVo;
    }

    default <T> T validate(String body, Class<T> clazz) {
        T fo = JSON.parseObject(body, clazz);
        ValidateUtils.validate(fo);
        return fo;
    }

    /**
     * 登录
     *
     * @param body   登录对象
     * @param client client
     * @return 登录验证信息
     */
    SystemUser login(String body, SystemClientVo client);

}
