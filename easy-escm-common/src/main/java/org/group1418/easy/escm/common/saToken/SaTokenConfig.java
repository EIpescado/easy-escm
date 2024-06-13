package org.group1418.easy.escm.common.saToken;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.map.MapBuilder;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.config.properties.EasyEscmConfigProperties;
import org.group1418.easy.escm.common.enums.EasyEscmTipEnum;
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
    public static final Map<String, EasyEscmTipEnum> SA_LOGIN_CODE_MAP = MapBuilder.<String, EasyEscmTipEnum>create()
            .put(NotLoginException.NOT_TOKEN, EasyEscmTipEnum.TOKEN_INVALID)
            .put(NotLoginException.INVALID_TOKEN, EasyEscmTipEnum.TOKEN_INVALID)
            .put(NotLoginException.TOKEN_TIMEOUT, EasyEscmTipEnum.TOKEN_TIMEOUT)
            .put(NotLoginException.BE_REPLACED, EasyEscmTipEnum.BE_REPLACED)
            .put(NotLoginException.KICK_OUT, EasyEscmTipEnum.KICK_OUT)
            .put(NotLoginException.TOKEN_FREEZE, EasyEscmTipEnum.TOKEN_FREEZE)
            .put(NotLoginException.NO_PREFIX, EasyEscmTipEnum.TOKEN_PREFIX_ERROR)
            .build();

    @Bean
    @Primary
    public cn.dev33.satoken.config.SaTokenConfig getSaTokenConfigPrimary(EasyEscmConfigProperties easyEscmConfigProperties) {
        return easyEscmConfigProperties.getTokenConfig();
    }

    public static R<String> saTokenExceptionHandler(SaTokenException e) {
        //sa-token 的异常码
        int code = e.getCode();
        EasyEscmTipEnum customTipEnum = EasyEscmTipEnum.FAIL;
        if (e instanceof NotLoginException) {
            NotLoginException nle = (NotLoginException) e;
            String type = nle.getType();
            customTipEnum = SA_LOGIN_CODE_MAP.get(type);
            customTipEnum = customTipEnum != null ? customTipEnum : EasyEscmTipEnum.FAIL;
        }
        log.error("sa-token异常码[{}]",code);
        return R.fail(customTipEnum);
    }

}
