package org.group1418.easy.escm.common.config.properties;

import lombok.Data;

/**
 * EasyEscmAsyncConfig
 *
 * @author yq 2024/6/14 10:03
 */
@Data
public class EasyEscmAsyncConfig {
    /**
     * 线程池名称, 线程名称为 threadName_数字
     */
    private String threadName;
    /**
     * 核心线程数
     */
    private Integer coreSize = 2;
    /**
     * 线程池最大线程数
     */
    private Integer maxSize = 8;
    /**
     * 线程队列最大线程数
     */
    private Integer queueCapacity = 4096;
    /**
     * 线程池中线程最大空闲时间，默认：60，单位：秒
     */
    private Integer keepAliveSeconds = 60;
    /**
     * 核心线程是否允许超时，默认false
     */
    private boolean allowCoreThreadTimeout = true;
    /**
     * IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（必须设置setAwaitTerminationSeconds）
     */
    private boolean waitForTasksToCompleteOnShutdown = false;
    /**
     * 阻塞IOC容器关闭的时间，默认：10秒（必须设置setWaitForTasksToCompleteOnShutdown）
     */
    private int awaitTerminationSeconds = 10;
}
