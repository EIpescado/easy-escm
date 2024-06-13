package org.group1418.easy.escm.common.spring;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * spring 上下文
 *
 * @author yq
 */
@Slf4j
@Component
public class SpringContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * "@PostConstruct"注解标记的类中，由于ApplicationContext还未加载，导致空指针<br>
     * 因此实现BeanFactoryPostProcessor注入ConfigurableListableBeanFactory实现bean的操作
     */
    private static ConfigurableListableBeanFactory beanFactory;
    /**
     * Spring应用上下文环境
     */
    private static ApplicationContext applicationContext;
    private static final List<SpringApplicationCallBack> CALL_BACKS = new CopyOnWriteArrayList<>();
    private static boolean addCallback = true;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("注入[applicationContext]");
        SpringContextHolder.applicationContext = applicationContext;
        if (addCallback) {
            for (SpringApplicationCallBack callBack : SpringContextHolder.CALL_BACKS) {
                callBack.execute();
            }
            CALL_BACKS.clear();
        }
        SpringContextHolder.addCallback = false;
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("注入[beanFactory]");
        SpringContextHolder.beanFactory = beanFactory;
    }

    /**
     * 针对 某些初始化方法，在SpringContextHolder 未初始化时 提交回调方法。
     * 在SpringContextHolder 初始化后，进行回调使用
     *
     * @param callBack 回调函数
     */
    public synchronized static void addCallBacks(SpringApplicationCallBack callBack) {
        if (addCallback) {
            SpringContextHolder.CALL_BACKS.add(callBack);
        } else {
            callBack.execute();
        }
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param <T>   bean类型
     * @param name  Bean名称
     * @param clazz bean类型
     * @return Bean对象
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * spring上下文是否包含指定名称的bean
     *
     * @param name 名称
     * @return 是否包含
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param key 配置项key
     * @return 属性值
     */
    public static String getProperty(String key) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取SpringBoot 配置信息
     *
     * @param property   属性key
     * @param targetType 返回类型
     * @return /
     */
    public static <T> T getProperty(String property, Class<T> targetType) {
        return getProperty(property, null, targetType);
    }

    /**
     * 获取SpringBoot 配置信息
     *
     * @param property     属性key
     * @param defaultValue 默认值
     * @param targetType   返回类型
     * @return /
     */
    public static <T> T getProperty(String property, T defaultValue, Class<T> targetType) {
        return null == applicationContext ? null : applicationContext.getEnvironment().getProperty(property, targetType, defaultValue);
    }

    /**
     * 获取应用程序名称
     *
     * @return 应用程序名称
     * @since 5.7.12
     */
    public static String getApplicationName() {
        return getProperty("spring.application.name");
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     * @since 5.3.3
     */
    public static String[] getActiveProfiles() {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前的环境配置，当有多个环境配置时，只获取第一个
     *
     * @return 当前的环境配置
     * @since 5.3.3
     */
    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return ArrayUtil.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }

    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() throws UtilException {
        ConfigurableListableBeanFactory factory;
        if (null != beanFactory) {
            factory = beanFactory;
        } else {
            if (!(applicationContext instanceof ConfigurableApplicationContext)) {
                throw EasyEscmException.simple("No ConfigurableListableBeanFactory from context!");
            }
            factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        }

        return factory;
    }

    /**
     * 注册Bean, 此注册不会 动触发初始化方法（如 @PostConstruct 注解的方法或通过 init-method 指定的方法 或实现 InitializingBean）。
     * 只是将一个已经创建的对象注册为bean，而不会触发Spring的完整生命周期管理
     *
     * @param beanName bean名称
     * @param bean     bean对象
     * @param <T>      bean类型
     */
    public static <T> void registerBean(String beanName, T bean) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
        log.info("注入bean [{}]",beanName);
        log.info("自动注入[{}]", beanName);
    }

    /**
     * 注册bean 并触发Spring的完整生命周期管理
     * @param beanName  bean名称
     * @param object  bean对象
     */
    public static void registerBeanDefinition(String beanName, Object object) {
        log.info("自动注入[{}]", beanName);
        // 获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        // 创建bean信息
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(object.getClass());
        //延迟加载
        beanDefinitionBuilder.setLazyInit(true);
        // 动态注册bean
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    /**
     * 移除bean
     *
     * @param beanName bean名称
     */
    public static void unregisterBean(String beanName) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        if (factory instanceof DefaultSingletonBeanRegistry) {
            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) factory;
            registry.destroySingleton(beanName);
            log.info("移除注入bean [{}]",beanName);
        } else {
            log.info("移除bean [{}] 失败",beanName);
            throw EasyEscmException.simple("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
        }
    }

    public static void publishEvent(ApplicationEvent event) {
        if (null != applicationContext) {
            applicationContext.publishEvent(event);
        }

    }

    public static void publishEvent(Object event) {
        if (null != applicationContext) {
            applicationContext.publishEvent(event);
        }

    }

    /**
     * 获取aop代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * spring上下文初始化前回调,用于初始化一些配置
     *
     * @author yq
     */
    public interface SpringApplicationCallBack {
        /**
         * 回调执行方法
         */
        void execute();
    }
}
