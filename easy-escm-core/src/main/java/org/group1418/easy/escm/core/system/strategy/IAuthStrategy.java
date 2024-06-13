package org.group1418.easy.escm.core.system.strategy;

import com.alibaba.fastjson2.JSON;
import org.group1418.easy.escm.common.exception.SystemCustomException;
import org.group1418.easy.escm.common.spring.SpringContextHolder;
import org.group1418.easy.escm.common.utils.ValidateUtils;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemClientVo;

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
            throw SystemCustomException.i18n("auth.grant.type.not.support");
        }
        IAuthStrategy instance = SpringContextHolder.getBean(beanName, IAuthStrategy.class);
        return instance.login(body, client);
    }

    default <T> T validate(String body, Class<T> clazz){
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
    LoginVo login(String body, SystemClientVo client);

}
