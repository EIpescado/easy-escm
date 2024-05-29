package org.group1418.easy.escm.common.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.annotation.ApiEncrypt;
import org.group1418.easy.escm.common.config.properties.CustomConfigProperties;
import org.group1418.easy.escm.common.enums.CustomTipEnum;
import org.group1418.easy.escm.common.filter.wrapper.DecryptRequestBodyWrapper;
import org.group1418.easy.escm.common.filter.wrapper.EncryptResponseBodyWrapper;
import org.group1418.easy.escm.common.spring.SpringContextHolder;
import org.group1418.easy.escm.common.utils.PudgeUtil;
import org.group1418.easy.escm.common.wrapper.R;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Crypto 过滤器
 *
 * @author wdhcr
 */
@RequiredArgsConstructor
@Slf4j
public class CryptoFilter implements Filter {

    private final CustomConfigProperties properties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        boolean responseFlag = false;
        ServletRequest requestWrapper = null;
        EncryptResponseBodyWrapper responseBodyWrapper = null;
        CustomConfigProperties.ApiDecryptConfig apiDecryptConfig = properties.getApiDecryptConfig();

        // 是否为 json 请求
        if (StrUtil.startWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            // 是否为 put 或者 post 请求
            if (HttpMethod.POST.matches(servletRequest.getMethod()) || HttpMethod.PUT.matches(servletRequest.getMethod())) {
                // 是否存在加密标记
                String headerValue = servletRequest.getHeader(apiDecryptConfig.getHeaderName());
                // 获取加密注解
                ApiEncrypt apiEncrypt = this.getApiEncryptAnnotation(servletRequest);
                // 请求需解密但前端未传递加密标识
                if (StrUtil.isBlank(headerValue) && apiEncrypt != null) {
                    log.warn("[{}]api[{}]加密未传有效标识", PudgeUtil.getIp(servletRequest), servletRequest.getRequestURI());
                    //直接返回401
                    PudgeUtil.responseJson(servletResponse, R.<String>fail(CustomTipEnum.PERMISSION_DENIED));
                    return;
                }
                responseFlag = apiEncrypt != null && apiEncrypt.response();
                // 请求解密
                if (StrUtil.isNotBlank(headerValue)) {
                    try {
                        requestWrapper = new DecryptRequestBodyWrapper(servletRequest, apiDecryptConfig.getRequestPrivateKey(), apiDecryptConfig.getHeaderName());
                    } catch (CryptoException e) {
                        log.error("解密报文失败[{}]", e.getLocalizedMessage());
                        PudgeUtil.responseJson(servletResponse, R.failI18n("params.crypto.error"));
                        return;
                    }
                }
                // 判断是否响应加密
                if (responseFlag) {
                    responseBodyWrapper = new EncryptResponseBodyWrapper(servletResponse);
                }
            }
        }

        chain.doFilter(ObjectUtil.defaultIfNull(requestWrapper, request), ObjectUtil.defaultIfNull(responseBodyWrapper, response));

        if (responseFlag) {
            servletResponse.reset();
            // 对原始内容加密
            String encryptContent = responseBodyWrapper.getEncryptContent(servletResponse, apiDecryptConfig.getResponsePublicKey(), apiDecryptConfig.getHeaderName());
            // 对加密后的内容写出
            servletResponse.getWriter().write(encryptContent);
        }
    }

    /**
     * 获取 ApiEncrypt 注解
     */
    private ApiEncrypt getApiEncryptAnnotation(HttpServletRequest servletRequest) {
        RequestMappingHandlerMapping handlerMapping = SpringContextHolder.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        // 获取注解
        try {
            HandlerExecutionChain mappingHandler = handlerMapping.getHandler(servletRequest);
            if (mappingHandler != null) {
                Object handler = mappingHandler.getHandler();
                // 从handler获取注解
                if (handler instanceof HandlerMethod) {
                    return ((HandlerMethod) handler).getMethodAnnotation(ApiEncrypt.class);
                }
            }
        } catch (Exception e) {
            log.error("获取 ApiEncrypt 注解失败 [{}]", e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void destroy() {
    }
}
