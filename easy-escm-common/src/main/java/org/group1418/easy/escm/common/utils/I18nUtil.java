package org.group1418.easy.escm.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.spring.SpringContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * i18n 国际化工具
 *
 * @author yq 2024年5月29日 14:16:29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class I18nUtil {

    private static final MessageSource MESSAGE_SOURCE = SpringContextHolder.getBean(MessageSource.class);

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String getMessage(String code, Object... args) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        try {
            return MESSAGE_SOURCE.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            log.warn("[{}]无对应国际化", code);
            return code;
        }
    }
}
