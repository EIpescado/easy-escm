package org.group1418.easy.escm.common.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.group1418.easy.escm.common.cache.TenantRedisKeyPrefixNameMapper;
import org.group1418.easy.escm.common.config.properties.EasyEscmRedissonConfig;
import org.group1418.easy.escm.common.serializer.FastJson2JsonRedissonCodec;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

/**
 * redis配置
 *
 * @author yq 2020/09/18 14:11
 */
@Slf4j
@Configuration
@EnableCaching
@EnableConfigurationProperties({RedisProperties.class})
public class EasyEscmRedisConfig extends CachingConfigurerSupport {

    private final EasyEscmRedissonConfig easyEscmRedissonConfig;

    public EasyEscmRedisConfig(EasyEscmRedissonConfig easyEscmRedissonConfig) {
        this.easyEscmRedissonConfig = easyEscmRedissonConfig;
    }

    @Bean
    public RedisCacheService customRedisCacheService(RedissonClient redissonClient) {
        log.info("注入 customRedisCacheService");
        return new RedisCacheService(redissonClient);
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
        log.info("注入 默认redis errorHandler");
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }

        };
    }

    /**
     * 节点地址及密码通过此项设置进入 参照 RedissonAutoConfiguration,方便多环境配置
     *
     * @param redisProperties redis配置
     */
    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizers(RedisProperties redisProperties) {
        log.info("注入 redissonAutoConfigurationCustomizers [{}]", redisProperties.getHost());
        return config -> {
            FastJson2JsonRedissonCodec fastJson2Codec = new FastJson2JsonRedissonCodec(null, null);
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, fastJson2Codec, fastJson2Codec);
            //数据序列化和反序列化
            config.setCodec(codec)
                    .setThreads(easyEscmRedissonConfig.getThreads())
                    .setNettyThreads(easyEscmRedissonConfig.getNettyThreads())
                    //LUA脚本缓存
                    .setUseScriptCache(true);
            //redis cluster
            if (easyEscmRedissonConfig.getClusterServersConfig() != null) {
                List<String> nodes = redisProperties.getCluster().getNodes();
                ClusterServersConfig clusterServersConfig = config.useClusterServers();
                if (CollUtil.isNotEmpty(nodes)) {
                    nodes.forEach(node -> clusterServersConfig.addNodeAddress(buildNodeAddress(node, redisProperties.isSsl())));
                }
                clusterServersConfig.setPassword(redisProperties.getPassword())
                        .setNameMapper(new TenantRedisKeyPrefixNameMapper(easyEscmRedissonConfig.getKeyPrefix()))
                        .setTimeout(clusterServersConfig.getTimeout())
                        .setClientName(clusterServersConfig.getClientName())
                        .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                        .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                        .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                        .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                        .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                        .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                        .setReadMode(clusterServersConfig.getReadMode())
                        .setSubscriptionMode(clusterServersConfig.getSubscriptionMode());

            } else {
                EasyEscmRedissonConfig.SingleServerConfig singleServerConfig = easyEscmRedissonConfig.getSingleServerConfig();
                //单节点
                config.useSingleServer()
                        .setAddress(buildNodeAddress(redisProperties.getHost(), redisProperties.getPort(), redisProperties.isSsl()))
                        .setDatabase(redisProperties.getDatabase())
                        .setPassword(redisProperties.getPassword())
                        .setNameMapper(new TenantRedisKeyPrefixNameMapper(easyEscmRedissonConfig.getKeyPrefix()))
                        .setTimeout(singleServerConfig.getTimeout())
                        .setClientName(singleServerConfig.getClientName())
                        .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                        .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                        .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                        .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }
        };
    }

    /**
     * 构建redis集群节点对象
     *
     * @param host host
     * @param port 端口
     * @param ssl  是否启用ssl
     * @return node address
     */
    private String buildNodeAddress(String host, Integer port, boolean ssl) {
        String format = ssl ? "rediss://{}:{}" : "redis://{}:{}";
        return StrUtil.format(format, host, port);
    }

    /**
     * 构建redis集群节点对象
     *
     * @param node 节点 127.0.0.1:6379
     * @param ssl  是否启用ssl
     * @return node address
     */
    private String buildNodeAddress(String node, boolean ssl) {
        String format = ssl ? "rediss://{}" : "redis://{}";
        return StrUtil.format(format, node);
    }


    public static RedisTemplate<String, Object> getDefaultRedisTemplate(RedisConnectionFactory connectionFactory,
                                                                        GenericFastJsonRedisSerializer fastJsonRedisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        // key的序列化采用StringRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        template.setDefaultSerializer(fastJsonRedisSerializer);
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }
}
