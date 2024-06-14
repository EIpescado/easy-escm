package org.group1418.easy.escm.common.config;

import org.group1418.easy.escm.common.config.properties.EasyEscmApiDecryptConfig;
import org.group1418.easy.escm.common.filter.CryptoFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * @author yq 2024/4/8 16:33
 * @description FilterConfig 过滤器配置
 */
@Configuration
public class EasyEscmFilterConfig {

    @Bean
    public FilterRegistrationBean<CryptoFilter> cryptoFilterRegistration(EasyEscmApiDecryptConfig apiDecryptConfig) {
        FilterRegistrationBean<CryptoFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new CryptoFilter(apiDecryptConfig));
        registration.addUrlPatterns("/*");
        registration.setName("cryptoFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        return registration;
    }
}
