package org.group1418.easy.escm.common.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RBatch;
import org.redisson.api.RLock;
import org.redisson.api.RMapAsync;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 自定义redis过期时间缓存配置
 *
 * @author yq
 * @date 2020/10/16 09:07
 * @since V1.0.0
 */
public class RedisCacheService {

    private final static Logger logger = LoggerFactory.getLogger(RedisCacheService.class);
    /**
     * 缓存过期配置
     */
    private final static Map<String, CustomCacheConfig> CONFIG_MAP = new ConcurrentHashMap<>();
    private static final long DEFAULT_EXPIRE_SECONDS = -1L;
    private final RedissonClient redissonClient;
    private static final String REDIS_LOCK_SUFFIX = "_LOCK";
    private static final String INCREMENT_AND_TTL_LUA = "local errorCount = redis.call('GET',KEYS[1])" +
            "\nif (errorCount == false) then errorCount = 0" +
            "\nelse errorCount = tonumber(errorCount) end" +
            "\nerrorCount = tonumber(errorCount) + tonumber(ARGV[2])" +
            "\nredis.call('SETEX',KEYS[1],ARGV[1],errorCount)" +
            "\nreturn errorCount";
    private static final String AUTO_INC_SN = "auto_inc_sn";

    public RedisCacheService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 生成redisKey
     *
     * @param keys 建
     * @return 值
     */
    public static String buildKey(Object... keys) {
        Assert.notEmpty(keys);
        return StrUtil.join(StrUtil.COLON, keys);
    }

    /**
     * 设置缓存
     *
     * @param redisKey   缓存key
     * @param ttlSeconds 过期时间单位秒
     * @param getter     数据获取
     * @param lockKey    锁键值 使得同一时间只有一条线程去设置缓存,防止缓存击穿
     * @return 需要的数据
     */
    public <T> T set(String redisKey, long ttlSeconds, Supplier<T> getter, String lockKey) {
        if (getter == null) {
            return null;
        }
        boolean forever = ttlSeconds <= 0L;
        T result;
        //有锁则加锁
        if (StrUtil.isNotBlank(lockKey)) {
            //防止缓存击穿 即key突然失效,使得同一时间只有一条线程能访问数据库
            RLock lock = redissonClient.getLock(lockKey);
            try {
                lock.lock();
                logger.info("缓存: [{}]不存在, 调用getter", redisKey);
                result = getter.get();
                //将数据存入redis
                if (forever) {
                    redissonClient.getBucket(redisKey).set(result);
                } else {
                    redissonClient.getBucket(redisKey).set(result, Duration.ofSeconds(ttlSeconds));
                }
            } finally {
                //释放锁
                lock.unlock();
            }
        } else {
            result = getter.get();
            if (forever) {
                redissonClient.getBucket(redisKey).set(result);
            } else {
                redissonClient.getBucket(redisKey).set(result, Duration.ofSeconds(ttlSeconds));
            }
        }
        return result;
    }

    /**
     * 如果不存在则设置缓存
     *
     * @param name     缓存名称
     * @param key      缓存二级key
     * @param duration 过期时间
     * @param getter   获取数据函数
     * @param <T>      数据类型
     * @return 是否设置成功
     */
    public <T> boolean setNx(String name, String key, Duration duration, Supplier<T> getter) {
        String redisKey = buildKey(name, key);
        if (StrUtil.isEmpty(redisKey)) {
            return false;
        }
        return redissonClient.getBucket(redisKey).setIfAbsent(getter.get(), duration);
    }

    /**
     * 设置缓存
     *
     * @param name    缓存名称 用于取缓存过期时间
     * @param key     缓存名称后跟随的唯一表示 ,如 SYSTEM_USER_TREE:test 的test即为key
     * @param getter  数据获取
     * @param lockKey 锁标识
     * @return 需要的数据
     */
    public <T> T set(String name, String key, Supplier<T> getter, String lockKey) {
        String redisKey = buildKey(name, key);
        long ttlSeconds = getCacheExpireSeconds(name);
        return set(redisKey, ttlSeconds, getter, lockKey);
    }

    /**
     * 设置缓存
     *
     * @param name       缓存名称 用于取缓存过期时间
     * @param key        缓存名称后跟随的唯一表示
     * @param ttlSeconds 过期时间单位秒
     * @param getter     数据获取
     * @param lockKey    锁键值
     * @return 需要的数据
     */
    public <T> T set(String name, String key, long ttlSeconds, Supplier<T> getter, String lockKey) {
        String redisKey = buildKey(name, key);
        return set(redisKey, ttlSeconds, getter, lockKey);
    }

    /**
     * 获取缓存
     *
     * @param name 缓存名称 用于取缓存过期时间
     * @param key  缓存名称后跟随的唯一表示 ,如 SYSTEM_USER_TREE:test 的test即为key
     * @return 缓存
     */
    public <T> T get(String name, String key) {
        String redisKey = buildKey(name, key);
        return get(redisKey);
    }

    /**
     * 获取缓存
     *
     * @param redisKey 缓存key
     * @return 缓存数据
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String redisKey) {
        return (T) redissonClient.getBucket(redisKey).get();
    }


    /**
     * 删除缓存
     *
     * @param redisKeys 缓存key
     */
    public void del(String... redisKeys) {
        RBatch batch = redissonClient.createBatch();
        Arrays.asList(redisKeys).forEach(t -> batch.getBucket(t).deleteAsync());
        batch.execute();
    }

    /**
     * 删除缓存
     *
     * @param name 缓存名称
     * @param key  缓存名称后跟随的唯一表示 ,如 SYSTEM_USER_TREE:test 的test即为key
     */
    public void del(String name, String key) {
        String redisKey = buildKey(name, key);
        redissonClient.getBucket(redisKey).delete();
    }

    /**
     * 批量删除指定缓存名称的所有key
     *
     * @param name 缓存名称
     */
    public void batchDel(String name) {
        redissonClient.getKeys().deleteByPattern(buildKey(name, "*"));
    }

    /**
     * 批量获取key,按匹配
     *
     * @param pattern 正则
     * @return 返回
     */
    public Iterable<String> keys(String pattern) {
        return redissonClient.getKeys().getKeysByPattern(pattern);
    }

    /**
     * redis 缓存环绕增强,先从缓存拿,拿不到则调用实际方法获取数据并缓存
     *
     * @param redisKey   缓存key
     * @param ttlSeconds 过期时间 单位秒
     * @param getter     数据获取
     * @param <T>        类型
     * @param lockKey    锁键值,为空则在设置缓存时不加锁
     * @return T
     */
    public <T> T round(String redisKey, long ttlSeconds, Supplier<T> getter, String lockKey) {
        //从缓存中提取数据
        T result = get(redisKey);
        if (result != null) {
            return result;
        } else {
            // 从getter中获取并设置到缓存
            return set(redisKey, ttlSeconds, getter, lockKey);
        }
    }

    /**
     * redis 缓存环绕增强
     *
     * @param redisKey     缓存key
     * @param ttlSeconds   过期时间 单位秒
     * @param getter       数据获取
     * @param resultCanUse 判断result是否有效
     * @param <T>          类型
     * @param lockKey      锁键值,为空则在设置缓存时不加锁
     * @return T
     */
    public <T> T round(String redisKey, long ttlSeconds, Predicate<T> resultCanUse, Supplier<T> getter, String lockKey) {
        //从缓存中提取数据
        T result = get(redisKey);
        if (result != null) {
            if (resultCanUse != null) {
                if (resultCanUse.test(result)) {
                    return result;
                }
                return set(redisKey, ttlSeconds, getter, lockKey);
            }
            return result;
        } else {
            // 从getter中获取并设置到缓存
            return set(redisKey, ttlSeconds, getter, lockKey);
        }
    }

    /**
     * redis 缓存环绕增强
     *
     * @param redisKey 缓存key
     * @param getter   数据获取
     * @param lockKey  锁键值
     * @return 需要的数据
     */
    public <T> T round(String redisKey, Supplier<T> getter, String lockKey) {
        return round(redisKey, getCacheExpireSeconds(redisKey), getter, lockKey);
    }

    /**
     * 获取缓存,处理完成后移除缓存
     *
     * @param redisKey redis key
     * @param handler  数据处理
     * @return 缓存处理后的数据
     */
    public <T> T getAndDel(String redisKey, Consumer<T> handler) {
        T result = get(redisKey);
        if (handler != null) {
            handler.accept(result);
        }
        if (result != null) {
            redissonClient.getBucket(redisKey).delete();
        }
        return result;
    }

    /**
     * 获取缓存,处理完成后移除缓存
     *
     * @param redisKey redis key
     * @param function 数据处理
     * @return 缓存处理后的数据
     */
    public <T, R> R getAndDelWithBack(String redisKey, Function<T, R> function) {
        T result = get(redisKey);
        R r = null;
        if (function != null) {
            r = function.apply(result);
        }
        if (result != null) {
            redissonClient.getBucket(redisKey).delete();
        }
        return r;
    }

    /**
     * 获取缓存,处理完成后移除缓存
     *
     * @param name    缓存名称 用于取缓存过期时间
     * @param key     缓存名称后跟随的唯一表示 ,如 SYSTEM_USER_TREE::test 的test即为key
     * @param handler 缓存处理
     * @return 处理后的缓存数据
     */
    public <T> T getAndDel(String name, String key, Consumer<T> handler) {
        String redisKey = buildKey(name, key);
        return getAndDel(redisKey, handler);
    }

    /**
     * 设置hash缓存
     *
     * @param hash    哈希key
     * @param hk      缓存key
     * @param getter  数据获取
     * @param lockKey 锁键值 使得同一时间只有一条线程去设置缓存,防止缓存击穿
     * @return 需要的数据
     */
    public <T> T hSet(String hash, String hk, Supplier<T> getter, String lockKey) {
        T result = null;
        if (getter != null) {
            //有锁则加锁
            if (StrUtil.isNotBlank(lockKey)) {
                //防止缓存击穿 即key突然失效,使得同一时间只有一条线程能访问数据库
                RLock lock = redissonClient.getLock(lockKey);
                try {
                    lock.lock();
                    logger.info("缓存: [{}]不存在, 调用getter", hk);
                    result = getter.get();
                    if (result != null) {
                        //将数据存入redis
                        redissonClient.getMap(hash).put(hk, result);
                    }
                    return result;
                } finally {
                    //释放锁
                    lock.unlock();
                }
            } else {
                result = getter.get();
                if (result != null) {
                    //将数据存入redis
                    redissonClient.getMap(hash).put(hk, result);
                }
            }
        }
        return result;
    }

    /**
     * redis 缓存环绕增强
     *
     * @param redisKey 缓存key
     * @param hashKey  hashKey
     * @param getter   数据获取
     * @param lockKey  锁键值 使得同一时间只有一条线程去设置缓存,防止缓存击穿
     * @return 需要的数据
     */
    @SuppressWarnings("unchecked")
    public <T> T hashRound(String hashKey, String redisKey, Supplier<T> getter, String lockKey) {
        //从缓存中提取数据
        T result = (T) redissonClient.getMap(hashKey).get(redisKey);
        if (result != null) {
            return result;
        } else {
            return hSet(hashKey, redisKey, getter, lockKey);
        }
    }

    /**
     * 是否存在指定缓存
     *
     * @param name 缓存名称 用于取缓存过期时间
     * @param key  缓存名称后跟随的唯一表示 ,如 SYSTEM_USER_TREE:test 的test即为key
     * @return boolean 是否存在
     */
    public boolean exists(String name, String key) {
        String redisKey = buildKey(name, key);
        return exists(redisKey);
    }

    /**
     * 是否存在指定缓存
     *
     * @param redisKey 缓存key
     * @return boolean 是否存在
     */
    public boolean exists(String redisKey) {
        long l = redissonClient.getKeys().countExists(redisKey);
        return l > 0;
    }

    /**
     * 获取指定key的过期时间(毫秒)
     *
     * @param name 缓存名称 用于取缓存过期时间
     * @param key  缓存名称后跟随的唯一表示 ,如 SYSTEM_USER_TREEtest 的test即为key
     * @return 过期时间 单位s redis 2.8之后 若key不存在则返回 -2
     */
    public long ttl(String name, String key) {
        String redisKey = buildKey(name, key);
        return redissonClient.getBucket(redisKey).remainTimeToLive();
    }

    /**
     * 获取指定key的过期时间(毫秒)
     *
     * @param redisKey 缓存key
     * @return 过期时间 单位s redis 2.8之后 若key不存在则返回 -2
     */
    public long ttl(String redisKey) {
        return redissonClient.getBucket(redisKey).remainTimeToLive();
    }

    /**
     * 获取缓存
     *
     * @param hash hash
     * @param hk   hk
     * @return 缓存数据
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String hash, String hk) {
        return (T) redissonClient.getMap(hash).get(hk);
    }

    /**
     * 删除缓存
     *
     * @param hashKey   h
     * @param redisKeys key
     */
    public void hDel(String hashKey, Object... redisKeys) {
        if (ArrayUtil.isNotEmpty(redisKeys)) {
            RBatch batch = redissonClient.createBatch();
            RMapAsync<String, Object> rMap = batch.getMap(hashKey);
            Arrays.stream(redisKeys)
                    .filter(o -> ObjectUtil.isNotNull(o) && StrUtil.isNotEmpty(o.toString()))
                    .map(Object::toString).forEach(rMap::removeAsync);
            batch.execute();
        }
    }

    /**
     * 指定hash key是否存在
     *
     * @param hashKey hash名称
     * @param key     key值
     * @return 是否存在
     */
    public boolean hExists(String hashKey, Object key) {
        return redissonClient.getMap(hashKey).containsKey(key);
    }

    /**
     * hash key对应值不存在,则调用实际方法获取值并存入
     *
     * @param hashKey hash key
     * @param key     key
     * @param getter  获取数据函数
     * @param <T>     类型
     */
    public <T> void hashSetNx(String hashKey, Object key, Supplier<T> getter) {
        if (!hExists(hashKey, key) && getter != null) {
            redissonClient.getMap(hashKey).putIfAbsent(key, getter.get());
        }
    }

    /**
     * 获取缓存,处理完成后移除缓存
     *
     * @param hash    hash
     * @param hk      hk
     * @param handler 缓存处理
     * @return 处理后的缓存数据
     */
    public <T> T hGetAndDel(String hash, String hk, Consumer<T> handler) {
        T result = hGet(hash, hk);
        if (handler != null) {
            handler.accept(result);
        }
        hDel(hash, hk);
        return result;
    }


    /**
     * 设置key过期时间
     *
     * @param name     缓存名称
     * @param key      缓存名称后跟随的唯一表示 ,如 SYSTEM_USER_TREE:test 的test即为key
     * @param duration 过期时间
     */
    public void expire(String name, String key, Duration duration) {
        String redisKey = buildKey(name, key);
        expire(redisKey, duration);
    }

    /**
     * 设置key过期时间
     *
     * @param redisKey 缓存key
     * @param duration 过期时间
     */
    public void expire(String redisKey, Duration duration) {
        redissonClient.getBucket(redisKey).expire(duration);
    }

    /**
     * 从list获取数据,从右边移除一个元素
     *
     * @param redisKey list key
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T rightPop(String redisKey) {
        return (T) redissonClient.getDeque(redisKey).pollLast();
    }

    /**
     * push数据到list, 从左边添加一个元素
     *
     * @param redisKey list key
     * @param value    push的数据
     */
    public void leftPush(String redisKey, Object value) {
        redissonClient.getDeque(redisKey).addFirst(value);
    }

    /**
     * push数据到list
     *
     * @param redisKey list key
     * @param value    push的数据
     */
    public void leftPushAll(String redisKey, Object... value) {
        redissonClient.getDeque(redisKey).addFirst(value);
    }

    /**
     * 获取指定 List里元素个数
     *
     * @param redisKey list key
     * @return List里元素个数
     */
    public int lLen(String redisKey) {
        return redissonClient.getDeque(redisKey).size();
    }

    /**
     * 将name 过期时间配置存入本地
     *
     * @param name cache name
     * @param ttl  过期时间
     */
    public void put(String name, Duration ttl) {
        long seconds = ttl.getSeconds();
        if (seconds <= 0) {
            seconds = -1;
        }
        CONFIG_MAP.put(name, new CustomCacheConfig(name, seconds));
    }

    public void put(String name) {
        CONFIG_MAP.put(name, new CustomCacheConfig(name, -1L));
    }

    /**
     * 获取指定 cache name 或 key 配置的过期时间
     *
     * @param nameOrKey cache name 或 key
     * @return 过期时间 单位秒
     */
    public long getCacheExpireSeconds(String nameOrKey) {
        CustomCacheConfig config = CONFIG_MAP.get(nameOrKey);
        return config != null ? config.getSeconds() : DEFAULT_EXPIRE_SECONDS;
    }

    /**
     * 生成锁的key值
     *
     * @param nameOrKey 缓存名称或key
     * @return 锁的hash值
     */
    public RLock getLock(String nameOrKey) {
        return redissonClient.getLock(nameOrKey + REDIS_LOCK_SUFFIX);
    }

    /**
     * 锁并执行
     *
     * @param nameOrKey        锁名称
     * @param haveLockFun      获取到锁执行的函数
     * @param doNotHaveLockFun 未获取到锁时执行的函数
     */
    public void lockProcess(String nameOrKey, VoidFunc0 haveLockFun, VoidFunc0 doNotHaveLockFun) {
        RLock rLock = redissonClient.getLock(nameOrKey + REDIS_LOCK_SUFFIX);
        boolean hadLock = false;
        try {
            //尝试获取锁 30s自动释放
            hadLock = rLock.tryLock();
            if (!hadLock) {
                doNotHaveLockFun.callWithRuntimeException();
            } else {
                haveLockFun.callWithRuntimeException();
            }
        } finally {
            if (hadLock) {
                rLock.unlock();
            }
        }
    }

    /**
     * 锁并执行
     *
     * @param nameOrKey        锁名称
     * @param haveLockFun      获取到锁执行的函数
     * @param doNotHaveLockFun 未获取到锁时执行的函数
     */
    public <R> R lockProcess(String nameOrKey, Func0<R> haveLockFun, VoidFunc0 doNotHaveLockFun) {
        RLock rLock = redissonClient.getLock(nameOrKey + REDIS_LOCK_SUFFIX);
        boolean hadLock = false;
        try {
            //尝试获取锁 30s自动释放
            hadLock = rLock.tryLock();
            if (!hadLock) {
                doNotHaveLockFun.callWithRuntimeException();
            } else {
                return haveLockFun.callWithRuntimeException();
            }
        } finally {
            if (hadLock) {
                rLock.unlock();
            }
        }
        return null;
    }

    /**
     * 联锁并执行
     *
     * @param lockKeyPrefix    锁前缀
     * @param lockKeys         锁名称
     * @param haveLockFun      获取到锁执行的函数
     * @param doNotHaveLockFun 未获取到锁时执行的函数
     */
    public void mulLockProcess(String lockKeyPrefix, Collection<String> lockKeys, VoidFunc0 haveLockFun, VoidFunc0 doNotHaveLockFun) {
        RedissonMultiLock lock = new RedissonMultiLock(lockKeys.stream()
                .map(key -> redissonClient.getLock(buildKey(lockKeyPrefix, key, REDIS_LOCK_SUFFIX)))
                .toArray(RLock[]::new)
        );
        boolean hadLock = false;
        try {
            //尝试获取锁 30s自动释放
            hadLock = lock.tryLock();
            if (!hadLock) {
                doNotHaveLockFun.callWithRuntimeException();
            } else {
                haveLockFun.callWithRuntimeException();
            }
        } finally {
            if (hadLock) {
                lock.unlock();
            }
        }
    }

    /**
     * 联锁并执行
     *
     * @param lockKeyPrefix    锁前缀
     * @param lockKeys         锁名称
     * @param haveLockFun      获取到锁执行的函数
     * @param doNotHaveLockFun 未获取到锁时执行的函数
     */
    public <R> R mulLockProcess(String lockKeyPrefix, Collection<String> lockKeys, Func0<R> haveLockFun, VoidFunc0 doNotHaveLockFun) {
        RedissonMultiLock lock = new RedissonMultiLock(lockKeys.stream()
                .map(key -> redissonClient.getLock(buildKey(lockKeyPrefix, key, REDIS_LOCK_SUFFIX)))
                .toArray(RLock[]::new)
        );
        boolean hadLock = false;
        try {
            //尝试获取锁 30s自动释放
            hadLock = lock.tryLock();
            if (!hadLock) {
                doNotHaveLockFun.callWithRuntimeException();
            } else {
                return haveLockFun.callWithRuntimeException();
            }
        } finally {
            if (hadLock) {
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * 执行Lua脚本
     *
     * @param lua        脚本
     * @param returnType 返回值类型
     * @param keys       keys
     * @param args       参数
     * @param <T>        返回类型
     * @return 返回
     */
    public <T> T executeLua(String lua, RScript.ReturnType returnType, List<Object> keys, Object... args) {
        RScript script = redissonClient.getScript();
        return script.eval(RScript.Mode.READ_WRITE, lua, returnType, keys, args);
    }

    /**
     * 批量获取hash 缓存值
     *
     * @param hashKey hash key
     * @param keys    key
     * @param <T>     响应类型
     * @return 结果集
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> hmGet(String hashKey, List<?> keys) {
        Map<Object, Object> all = redissonClient.getMap(hashKey).getAll(new HashSet<>(keys));
        if (MapUtil.isNotEmpty(all)) {
            return (List<T>) ListUtil.toList(all.values());
        }
        return ListUtil.empty();
    }

    /**
     * 批量设置hash 缓存值
     *
     * @param hashKey   hash key
     * @param keyValues 只
     */
    public void hmSet(String hashKey, Map<String, Object> keyValues) {
        redissonClient.getMap(hashKey).putAll(keyValues);
    }

    /**
     * 自增序列号 形如 DC20240113 0001
     *
     * @param prefix   号码前缀
     * @param len      长度,不够前补0
     * @param duration 过期时间
     * @return 号码
     */
    public String incSn(String prefix, int len, Duration duration) {
        Long increment = incrementAndTtl(AUTO_INC_SN, prefix, Long.valueOf(duration.getSeconds()).intValue(), 1);
        return StrBuilder.create(prefix, StrUtil.fillBefore(String.valueOf(increment), '0', len)).toString();
    }

    /**
     * redis 自增值
     *
     * @param name key前缀
     * @param key  key
     * @param step 步长
     * @return 前值
     */
    public Long increment(String name, String key, long step) {
        return redissonClient.getAtomicLong(buildKey(name, key)).addAndGet(step);
    }

    /**
     * redis 自增并设置ttl
     *
     * @param name       key前缀
     * @param key        key
     * @param ttlSeconds ttl时间,单位秒
     * @param step       步长
     * @return 当前值
     */
    public Long incrementAndTtl(String name, String key, Integer ttlSeconds, Integer step) {
        String redisKey = buildKey(name, key);
        List<Object> keys = CollUtil.newArrayList(redisKey);
        return executeLua(INCREMENT_AND_TTL_LUA, RScript.ReturnType.INTEGER, keys, ttlSeconds, step);
    }

    /**
     * 自定义缓存名称 2020/10/15 17:51
     *
     * @author yq
     */
    @Data
    @AllArgsConstructor
    public static class CustomCacheConfig {
        /**
         * cache name
         */
        private String name;
        /**
         * 缓存过期时间 秒
         */
        private Long seconds;
    }

}
