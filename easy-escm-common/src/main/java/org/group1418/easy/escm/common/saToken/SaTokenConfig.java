package org.group1418.easy.escm.common.saToken;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.map.MapBuilder;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.config.properties.CustomConfigProperties;
import org.group1418.easy.escm.common.enums.CustomTipEnum;
import org.group1418.easy.escm.common.wrapper.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

/**
 * SaTokenConfig
 *
 * @author yq 2024/2/21 15:17
 */
@Configuration
@Slf4j
public class SaTokenConfig {

    /**
     * SaTokenException 转自定义代码
     */
    public static final Map<String, CustomTipEnum> SA_LOGIN_CODE_MAP = MapBuilder.<String, CustomTipEnum>create()
            .put(NotLoginException.NOT_TOKEN, CustomTipEnum.TOKEN_INVALID)
            .put(NotLoginException.INVALID_TOKEN, CustomTipEnum.TOKEN_INVALID)
            .put(NotLoginException.TOKEN_TIMEOUT, CustomTipEnum.TOKEN_TIMEOUT)
            .put(NotLoginException.BE_REPLACED, CustomTipEnum.BE_REPLACED)
            .put(NotLoginException.KICK_OUT, CustomTipEnum.KICK_OUT)
            .put(NotLoginException.TOKEN_FREEZE, CustomTipEnum.TOKEN_FREEZE)
            .put(NotLoginException.NO_PREFIX, CustomTipEnum.TOKEN_PREFIX_ERROR)
            .build();

    @Bean
    @Primary
    public cn.dev33.satoken.config.SaTokenConfig getSaTokenConfigPrimary(CustomConfigProperties customConfigProperties) {
        return customConfigProperties.getTokenConfig();
    }

    public static R<String> saTokenExceptionHandler(SaTokenException e) {
        //sa-token 的异常码
        int code = e.getCode();
        CustomTipEnum customTipEnum = CustomTipEnum.FAIL;
        if (e instanceof NotLoginException) {
            NotLoginException nle = (NotLoginException) e;
            String type = nle.getType();
            customTipEnum = SA_LOGIN_CODE_MAP.get(type);
            customTipEnum = customTipEnum != null ? customTipEnum : CustomTipEnum.FAIL;
        }
        log.error("sa-token异常码[{}]",code);
        return R.fail(customTipEnum);
    }

}
