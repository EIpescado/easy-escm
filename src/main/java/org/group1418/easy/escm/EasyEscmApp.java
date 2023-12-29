package org.group1418.easy.escm;

import org.group1418.easy.escm.config.properties.CustomConfigProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author yq 2023/12/27 16:08
 * @description EasyEscmApp
 */
@SpringBootApplication
@EnableConfigurationProperties(CustomConfigProperties.class)
@MapperScan(basePackages = "org.group1418.easy.escm.*.*.mapper")
public class EasyEscmApp {

    @Bean
    public ServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
        //允许查询参数中包含指定特殊字符 设置tomcat参数 The valid characters are defined in RFC 7230 and RFC 3986
        fa.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]{}"));
        return fa;
    }

    public static void main(String[] args) {
       SpringApplication.run(EasyEscmApp.class, args);
    }
}
