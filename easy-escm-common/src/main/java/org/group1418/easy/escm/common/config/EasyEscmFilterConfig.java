package org.group1418.easy.escm.common.config;

import org.group1418.easy.escm.common.config.properties.EasyEscmApiDecryptProp;
import org.group1418.easy.escm.common.filter.CryptoFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * FilterConfig 过滤器配置
 * @author yq 2024/4/8 16:33
 */
@Configuration
public class EasyEscmFilterConfig {

    /**
     * 接口参数及响应加密filter
     * @param apiDecryptConfig 加密配置
     * @return filter
     */
    @Bean
    public FilterRegistrationBean<CryptoFilter> cryptoFilterRegistration(EasyEscmApiDecryptProp apiDecryptConfig) {
        FilterRegistrationBean<CryptoFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new CryptoFilter(apiDecryptConfig));
        registration.addUrlPatterns("/*");
        registration.setName("cryptoFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        return registration;
    }
}
