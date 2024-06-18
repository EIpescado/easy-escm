package org.group1418.easy.escm.common.config;

import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.group1418.easy.escm.common.config.properties.EasyEscmProp;
import org.group1418.easy.escm.common.config.properties.EasyEscmTenantProp;
import org.group1418.easy.escm.common.spring.SpringContextHolder;
import org.group1418.easy.escm.common.tenant.EasyEscmTenantLineHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;


/**
 * mybatis-plus config
 *
 * @author yq 2021年4月14日 11:25:49
 */
@Slf4j
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("${mybatis-plus.mapper-package}")
@Configuration
public class EasyEscmMybatisPlusConfig {


    public EasyEscmMybatisPlusConfig() {
        log.info("注入 mybatisPlusInterceptor, metaObjectHandler");
    }

    /**
     * mybatis-plus插件
     */
    @Bean
    @ConditionalOnClass(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(EasyEscmProp easyEscmProp, EasyEscmTenantProp easyEscmTenantProp) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户插件 必须放第一位
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor(new EasyEscmTenantLineHandler(easyEscmTenantProp));
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        String dbType = easyEscmProp.getDbType();
        //todo 数据权限处理
        //分页插件 及 合理化
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.getDbType(dbType));
        paginationInnerInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        //乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * 使用网卡信息绑定雪花生成器
     * 防止集群雪花ID重复
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        return new DefaultIdentifierGenerator(NetUtil.getLocalhost());
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
