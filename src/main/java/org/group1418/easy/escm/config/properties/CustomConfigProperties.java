package org.group1418.easy.escm.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author yq 2021/5/21 20:02
 * @description CustomMybatisPlusConfigProperties
 */
@ConfigurationProperties(prefix = "easy.escm")
@Data
public class CustomConfigProperties {

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


}
