package org.group1418.easy.escm.common.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * CustomMybatisPlusConfigProperties 自定义配置
 *
 * @author yq 2021/5/21 20:02
 */
@ConfigurationProperties(prefix = "easy.escm")
@Data
@Component
public class EasyEscmConfig {

    /**
     * 系统名称
     */
    private String name = "easy-escm";

    /**
     * 日志文件路径
     */
    private String logPath;
    /**
     * 日志打印级别
     */
    private String logLevel;

    /**
     * 数据库类型, 多个用逗号相隔
     */
    private String dbType = "mysql";

    /**
     * 新增用户的默认密码
     */
    private String userDefaultPassword = "123456";

    /**
     * 登录失败 最大错误次数
     */
    private Integer loginMaxRetryCount = 5;

    /**
     * 登录失败 超过最大错误次数 锁定时间,单位分钟
     */
    private Integer loginLockTime = 10;

    /**
     * 异步线程池配置, 自动注入spring上下文,系统中止自动关闭, 注入使用需配合@Lazy
     */
    private List<EasyEscmAsyncConfig> asyncConfigs;

}
