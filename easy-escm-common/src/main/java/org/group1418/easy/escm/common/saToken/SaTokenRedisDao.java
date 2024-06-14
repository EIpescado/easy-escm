package org.group1418.easy.escm.common.saToken;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * SaTokenDaoRedis sa-token redis实现
 *
 * @author yq 2024/2/20 10:49
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SaTokenRedisDao implements SaTokenDao {

    private final RedisCacheService redisCacheService;

    @Override
    public String get(String key) {
        return redisCacheService.get(key);
    }

    @Override
    public void set(String key, String value, long timeout) {
        if (timeout != 0L && timeout > -2L) {
            this.redisCacheService.set(key, timeout, () -> value, null);
        }
    }

    @Override
    public void update(String key, String value) {
        long expire = this.getTimeout(key);
        if (expire != -2L) {
            this.set(key, value, expire);
        }
    }

    @Override
    public void delete(String key) {
        redisCacheService.del(key);
    }

    @Override
    public long getTimeout(String key) {
        return redisCacheService.ttl(key) / 1000;
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        if (timeout == -1L) {
            long expire = this.getTimeout(key);
            if (expire != -1L) {
                this.set(key, this.get(key), timeout);
            }
        } else {
            redisCacheService.expire(key, Duration.ofSeconds(timeout));
        }
    }

    @Override
    public Object getObject(String key) {
        return redisCacheService.get(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout != 0L && timeout > -2L) {
            this.redisCacheService.set(key, timeout, () -> object, null);
        }
    }

    @Override
    public void updateObject(String key, Object object) {
        long expire = this.getObjectTimeout(key);
        if (expire != -2L) {
            this.setObject(key, object, expire);
        }
    }

    @Override
    public void deleteObject(String key) {
        redisCacheService.del(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return redisCacheService.ttl(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        if (timeout == -1L) {
            long expire = this.getObjectTimeout(key);
            if (expire != -1L) {
                this.setObject(key, this.getObject(key), timeout);
            }
        } else {
            redisCacheService.expire(key, Duration.ofSeconds(timeout));
        }
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Iterable<String> keys = redisCacheService.keys(prefix + "*" + keyword + "*");
        if (CollUtil.isEmpty(keys)) {
            return ListUtil.empty();
        }
        List<String> list = ListUtil.toList(keys);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}
