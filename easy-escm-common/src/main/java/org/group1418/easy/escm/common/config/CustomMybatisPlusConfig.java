package org.group1418.easy.escm.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.group1418.easy.escm.common.config.properties.CustomConfigProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;


/**
 * mybatis-plus config
 * @author yq
 * @date 2021年4月14日 11:25:49
 * @since V1.0.0
 */
@Configuration
@Slf4j
@AutoConfigureAfter({MybatisPlusAutoConfiguration.class})
public class CustomMybatisPlusConfig {


    public CustomMybatisPlusConfig() {
        log.info("注入 mybatisPlusInterceptor, metaObjectHandler");
    }

    /**
     * mybatis-plus分页插件
     */
    @Bean
    @ConditionalOnClass(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(CustomConfigProperties plusConfigProperties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        String dbType = plusConfigProperties.getDbType();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.getDbType(dbType)));
        return interceptor;
    }

    /**
     * 自动填充值
     */
    @Bean
    @ConditionalOnClass(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            private static final String CREATE_TIME = "createTime";
            private static final String UPDATE_TIME = "updateTime";
            private static final String CREATOR_ID = "creatorId";
            private static final String CREATOR = "creator";
            private static final String UPDATER_ID = "updaterId";
            private static final String UPDATER = "updater";
            private static final String ENABLED = "enabled";

            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = null;
                this.setFieldValByName(ENABLED, true, metaObject);
                if (getFieldValByName(CREATE_TIME, metaObject) == null) {
                    now = LocalDateTime.now();
                    this.setFieldValByName(CREATE_TIME, now, metaObject);
                }
                if (getFieldValByName(UPDATE_TIME, metaObject) == null) {
                    this.setFieldValByName(UPDATE_TIME, now != null ? now : LocalDateTime.now(), metaObject);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
            }
        };
    }
}
