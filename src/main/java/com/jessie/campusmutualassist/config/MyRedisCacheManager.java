package com.jessie.campusmutualassist.config;

import com.jessie.campusmutualassist.cache.CacheContainer;
import com.jessie.campusmutualassist.cache.CacheItemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
public class MyRedisCacheManager extends RedisCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(MyRedisCacheManager.class);
    private final RedisCacheWriter cacheWriter;
    private final RedisCacheConfiguration defaultCacheConfig;
    private final Map<String, RedisCacheConfiguration> initialCacheConfigurations;
    /**
     * 缓存参数的分隔符
     * 数组元素0=缓存的名称
     * 数组元素1=缓存过期时间TTL
     * 数组元素2=缓存在多少秒开始主动失效来强制刷新
     */
    private static final String separator = "#";

    /**
     * 缓存主动在失效前强制刷新缓存的时间
     * 单位：秒
     */
    private static final long preloadSecondTime=0;
    private RedisOperations redisOperations;


    public MyRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
        this.initialCacheConfigurations=new LinkedHashMap<>();
    }

    public MyRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheNames);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
        this.initialCacheConfigurations=new LinkedHashMap<>();
    }

    public MyRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, boolean allowInFlightCacheCreation, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheNames);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
        this.initialCacheConfigurations=new LinkedHashMap<>();
    }

    public MyRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
        this.initialCacheConfigurations=initialCacheConfigurations;
    }

    public MyRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
        this.initialCacheConfigurations=initialCacheConfigurations;
    }

    /**
     * 这个构造方法最重要
     **/
    public MyRedisCacheManager(RedisConnectionFactory redisConnectionFactory, RedisCacheConfiguration cacheConfiguration) {
        this(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),cacheConfiguration);
    }
    public MyRedisCacheManager(
            RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfig,
            RedisOperations redisOperations,
            List<CacheItemConfig> cacheItemConfigList,
            RedisSerializationContext.SerializationPair<Object> pair) {
        this(
                cacheWriter,
                defaultCacheConfig,
                cacheItemConfigList
                        .stream()
                        .collect(Collectors.toMap(CacheItemConfig::getName, cacheItemConfig -> RedisCacheConfiguration
                                .defaultCacheConfig()
                                .serializeValuesWith(pair)
                                .entryTtl(Duration.ofSeconds(cacheItemConfig.getExpiryTimeSecond()))
                               ))
        );
        this.redisOperations=redisOperations;
        CacheContainer.init(cacheItemConfigList);
        logger.info("MyRedisCacheManager running");

    }
    public MyRedisCacheManager(
            RedisConnectionFactory connectionFactory,
            RedisOperations redisOperations,
            List<CacheItemConfig> cacheItemConfigList) {

        this(
                RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(40)),
                cacheItemConfigList
                        .stream()
                        .collect(Collectors.toMap(CacheItemConfig::getName,cacheItemConfig -> {
                            RedisCacheConfiguration cacheConfiguration =
                                    RedisCacheConfiguration
                                            .defaultCacheConfig()
                                            .entryTtl(Duration.ofSeconds(cacheItemConfig.getExpiryTimeSecond())).prefixCacheNameWith(cacheItemConfig.getName());
                            return cacheConfiguration;
                        }))
        );
        this.redisOperations=redisOperations;
        CacheContainer.init(cacheItemConfigList);
    }
    public MyRedisCacheManager(
            RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfig,
            RedisOperations redisOperations,
            Map<String,RedisCacheConfiguration> initialCacheConfigurations) {
        this(
                cacheWriter,
                defaultCacheConfig,
                initialCacheConfigurations);
        this.redisOperations=redisOperations;
        logger.info("MyRedisCacheManager running");
        logger.info(super.getCacheConfigurations().toString());//空的，为什么？

    }
    //备注一下：如果要启用时间自定义，下面的函数需要修改
    //覆盖父类创建RedisCache
    @Override
    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        return new MyRedisCache(name, cacheWriter, cacheConfig != null ? cacheConfig : defaultCacheConfig,redisOperations);
        //operations
    }

    @Override
    public Map<String, RedisCacheConfiguration> getCacheConfigurations() {
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>(getCacheNames().size());
        getCacheNames().forEach(it -> {
            RedisCache cache = MyRedisCache.class.cast(lookupCache(it));
            configurationMap.put(it, cache != null ? cache.getCacheConfiguration() : null);
        });
        return Collections.unmodifiableMap(configurationMap);
    }
    /*
    * 重写getCache实现缓存时间自定义
    * */

    //就这个方法导致全部是默认设置，估计是原作者的版本比较老是这样实现的没问题，但是新版本不再是这样了
//    @Override
//    public Cache getCache(String name) {
//
//        Cache cache = super.getCache(name);
//        if(null==cache){
//            return cache;
//        }
//        MyRedisCache redisCache= new MyRedisCache(
//                name,
//                this.cacheWriter,
//                this.defaultCacheConfig,
//                this.redisOperations
//        );
//
//        return redisCache;
//
//    }
}