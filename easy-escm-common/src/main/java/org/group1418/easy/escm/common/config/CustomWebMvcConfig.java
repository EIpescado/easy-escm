package org.group1418.easy.escm.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.record.DVALRecord;
import org.group1418.easy.escm.common.config.properties.CustomConfigProperties;
import org.group1418.easy.escm.common.enums.IBaseEnum;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;
import org.group1418.easy.escm.common.serializer.BaseEnum2KeyWriter;
import org.group1418.easy.escm.common.serializer.LocalDateWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.Servlet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * web上下文配置
 *
 * @author yq
 * @date 2018/04/18 14:05
 * @since V1.0.0
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurationSupport.class})
@Slf4j
public class CustomWebMvcConfig extends WebMvcConfigurationSupport {

    private final FastJsonHttpMessageConverter fastJsonHttpMessageConverter;
    private final CustomConfigProperties configProperties;

    public CustomWebMvcConfig(CustomConfigProperties configProperties) {
        this.fastJsonHttpMessageConverter = createDefaultFastJsonHttpMessageConverter();
        this.configProperties = configProperties;
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("注入 FastJsonHttpMessageConverter");
        converters.add(fastJsonHttpMessageConverter);
        //直接返回图片 消息转化器
        converters.add(new BufferedImageHttpMessageConverter());
    }

    @Bean
    @Primary
    public FastJsonHttpMessageConverter getDefaultFastJsonHttpMessageConverter() {
        return fastJsonHttpMessageConverter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能,除开 @SaIgnore 标识和 配置所有接口需登录,
        registry.addInterceptor(new SaInterceptor(handle ->
                SaRouter.match("/**").notMatch(configProperties.getTokenConfig().getNotCheckLoginPaths()).check(r -> StpUtil.checkLogin()))
        ).addPathPatterns("/**");
    }

    @Override
    public LocaleResolver localeResolver() {
        //根据请求头 Accept-Language 确定语言
        AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
        //设置支持的语言
        List<Locale> locales = new ArrayList<>();
        locales.add(Locale.SIMPLIFIED_CHINESE);
        locales.add(Locale.forLanguageTag("zh-HK"));
        locales.add(Locale.US);
        acceptHeaderLocaleResolver.setSupportedLocales(locales);
        //默认简体中文
        acceptHeaderLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return acceptHeaderLocaleResolver;
    }

    private FastJsonHttpMessageConverter createDefaultFastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //不可使用*/*,强制用户自己定义支持的MediaTypes
        fastConverter.setSupportedMediaTypes(CollUtil.toList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
        //全局配置
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setWriterFeatures(
                //是否输出值为null的字段,默认false
                JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.WriteNullListAsEmpty,
                //整型转字符串 防止前端精度丢失
                JSONWriter.Feature.WriteLongAsString,
                //字符串输出null值
                JSONWriter.Feature.WriteNullStringAsEmpty,
                //枚举默认toString
                JSONWriter.Feature.WriteEnumUsingToString
        );
        //全局日期格式
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //对一些类型注入默认的writer
        JSON.register(LocalDate.class, new LocalDateWriter());
        //注入所有IBaseEnum的writer
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper("org.group1418.easy.escm",IBaseEnum.class);
        BaseEnum2KeyWriter baseEnum2KeyWriter = new BaseEnum2KeyWriter();
        if(CollUtil.isNotEmpty(classes)){
            classes.forEach(c -> JSON.register(c, baseEnum2KeyWriter));
        }
        return fastConverter;
    }
}
