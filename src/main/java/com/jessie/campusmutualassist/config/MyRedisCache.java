package com.jessie.campusmutualassist.config;

import com.jessie.campusmutualassist.cache.CacheContainer;
import com.jessie.campusmutualassist.cache.CacheItemConfig;
import com.jessie.campusmutualassist.cache.CacheSupport;
import com.jessie.campusmutualassist.cache.helper.ApplicationContextHelper;
import com.jessie.campusmutualassist.cache.helper.ThreadTaskHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisOperations;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 参考资料： https://my.oschina.net/u/220938/blog/3196609
 * 感谢dalao的无偿分享！
 * */
public class MyRedisCache extends RedisCache {
    private static final Logger logger = LoggerFactory.getLogger(MyRedisCache.class);
    private final String name;
    private final RedisCacheWriter cacheWriter;
    private final ConversionService conversionService;
    private RedisOperations redisOperations;
    private static final Lock REFRESH_CACKE_LOCK = new ReentrantLock();


    protected MyRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.name = name;
        this.cacheWriter = cacheWriter;
        this.conversionService = cacheConfig.getConversionService();
    }

    public MyRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig, RedisOperations redisOperations) {
        super(name, cacheWriter, cacheConfig);
        this.name = name;
        this.cacheWriter = cacheWriter;
        this.conversionService = cacheConfig.getConversionService();
        this.redisOperations = redisOperations;
    }

    @Override
    public void evict(Object key) {
        if (key instanceof String) {
            String keyString = key.toString();
            if (StringUtils.endsWith(keyString, "*")) {
                evictLikeSuffix(keyString);
                return;
            }
            if (StringUtils.startsWith(keyString, "*")) {
                evictLikePrefix(keyString);
                return;
            }
        }
        super.evict(key);
    }

    /**
     * 前缀匹配
     *
     * @param key
     */
    public void evictLikePrefix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }
//    其实RedisCache里的clear方法本身是支持通配的，但是后面包装时去掉了这个特性，只保留了全部删除

    /**
     * 后缀匹配
     *
     * @param key
     */
    public void evictLikeSuffix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }

    @Override
    public ValueWrapper get(final Object key) {
        //工作正常
        ValueWrapper valueWrapper = super.get(key);
        if (null != valueWrapper) {
            CacheItemConfig cacheItemConfig = CacheContainer.getCacheItemConfigByCacheName(key.toString());
            long preLoadTimeSecond = cacheItemConfig.getPreLoadTimeSecond();
            ;
            String cacheKey = this.createCacheKey(key);
            Long ttl = this.redisOperations.getExpire(cacheKey);
            if (null != ttl && ttl <= preLoadTimeSecond) {
                logger.info("key:{} ttl:{} preloadSecondTime:{}", cacheKey, ttl, preLoadTimeSecond);
                if (ThreadTaskHelper.hasRunningRefreshCacheTask(cacheKey)) {
                    logger.info("do not need to refresh");
                } else {
                    ThreadTaskHelper.run(() -> {
                        try {
                            REFRESH_CACKE_LOCK.lock();
                            if (ThreadTaskHelper.hasRunningRefreshCacheTask(cacheKey)) {
                                logger.info("do not need to refresh");
                            } else {
                                logger.info("refresh key:{}", cacheKey);
                                MyRedisCache.this.getCacheSupport().refreshCacheByKey(MyRedisCache.super.getName(), key.toString());
                                //注意此处this的含义有歧义（默认是此处taskHelper的this） 所以前面需要指定MyRedisCache
                                ThreadTaskHelper.removeRefreshCacheTask(cacheKey);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            REFRESH_CACKE_LOCK.unlock();
                        }
                    });
                }
            }
        }
        return valueWrapper;
    }

    private CacheSupport getCacheSupport() {

        //居然是没有写@Compoent?????????????????????????????????????????????????????????????????
        return ApplicationContextHelper.getApplicationContext().getBean(CacheSupport.class);
    }


}
