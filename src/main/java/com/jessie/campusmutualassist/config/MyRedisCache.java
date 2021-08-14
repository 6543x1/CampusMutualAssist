package com.jessie.campusmutualassist.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
/*
* 参考资料： https://my.oschina.net/u/220938/blog/3196609
* 感谢dalao的无偿分享！
* */
public class MyRedisCache extends RedisCache {
    private final String name;
    private final RedisCacheWriter cacheWriter;
    private final ConversionService conversionService;


    protected MyRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.name=name;
        this.cacheWriter=cacheWriter;
        this.conversionService= cacheConfig.getConversionService();
    }

    @Override
    public void evict(Object key) {
        if(key instanceof  String){
            String keyString=key.toString();
            if(StringUtils.endsWith(keyString,"*")){
                evictLikeSuffix(keyString);
                return;
            }
            if(StringUtils.startsWith(keyString,"*")){
                evictLikePrefix(keyString);
                return;
            }
        }
        super.evict(key);
    }

    /**
     * 前缀匹配
     * @param key
     */
    public void evictLikePrefix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }
//    其实RedisCache里的clear方法本身是支持通配的，但是后面包装时去掉了这个特性，只保留了全部删除
    /**
     * 后缀匹配
     * @param key
     */
    public void evictLikeSuffix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }



}
