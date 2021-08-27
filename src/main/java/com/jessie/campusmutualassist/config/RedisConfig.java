package com.jessie.campusmutualassist.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jessie.campusmutualassist.cache.CacheItemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * redis配置类
 **/
@Configuration
@EnableCaching
@Import(DefaultListableBeanFactory.class)
@Slf4j
public class RedisConfig extends CachingConfigurerSupport
{

    @Autowired
    private RedisConnectionFactory factory;

    private static final Random random=new Random();


    @Bean
    public RedisTemplate<String, Object> redisTemplate()
    {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        FastJson2JsonRedisSerializer serializer=new FastJson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        serializer.setObjectMapper(mapper);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        //redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(String.class));
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate)
    {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate)
    {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate)
    {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate)
    {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate)
    {
        return redisTemplate.opsForZSet();
    }

    @Bean
    @Primary//当有多个管理器的时候，必须使用该注解在一个管理器上注释：表示该管理器为默认的管理器
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        //序列化方式2
        //FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);//JSONObject
        FastJson2JsonRedisSerializer fastJsonRedisSerializer=new FastJson2JsonRedisSerializer(Object.class);
        //换用了自定义的序列化器后，缓存突然就正常了不会报JSONObject cant be cast to PageInfo的bug了，我也搞不懂是什么原理，不知道有什么细节上的差别
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer);
        RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(pair)
                .entryTtl(Duration.ofSeconds(180));//7200



//        defaultCacheConfig = defaultCacheConfig.entryTtl();//设置过期时间
//        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));

        //初始化RedisCacheManager
//        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
        List<CacheItemConfig> cacheItemConfigs= new ArrayList<>();
        cacheItemConfigs.add(new CacheItemConfig("noticeCache",60+ random.nextInt(10),5));//86400
        cacheItemConfigs.add(new CacheItemConfig("classVotes",60+random.nextInt(10),5));//86400
        cacheItemConfigs.add(new CacheItemConfig("classSignIn",60,5));//200
        //RedisCacheManager cacheManager=new MyRedisCacheManager(redisCacheWriter,defaultCacheConfig);
//        Map<String, RedisCacheConfiguration> initialCacheConfiguration = new HashMap<String, RedisCacheConfiguration>() {{
//            put("noticeCache", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1))); //1小时
//            put("ClassVotes", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))); // 10分钟
//            // ...
//        }};
        //RedisCacheManager cacheManager=new MyRedisCacheManager(redisCacheWriter,defaultCacheConfig,initialCacheConfiguration);
        RedisCacheManager cacheManager=new MyRedisCacheManager(redisCacheWriter,defaultCacheConfig,redisTemplate(),cacheItemConfigs,pair);
        //这一步调用自定义的CacheManager，然后自定义CacheManager调用自定义的RedisCache，来达到删除时通配的效果
        //设置白名单---非常重要********
        /*
        使用fastjson的时候：序列化时将class信息写入，反解析的时候，
        fastjson默认情况下会开启autoType的检查，相当于一个白名单检查，
        如果序列化信息中的类路径不在autoType中，
        反解析就会报com.alibaba.fastjson.JSONException: autoType is not support的异常
        可参考 https://blog.csdn.net/u012240455/article/details/80538540
         */
        ParserConfig.getGlobalInstance().addAccept("com.jessie.campusmutualassist.entity");
//        RedisCacheManager cacheManager=RedisCacheManager
//                .builder(factory)
//                .cacheWriter(redisCacheWriter)
//                .cacheDefaults(defaultCacheConfig)
//                .withInitialCacheConfigurations(initialCacheConfiguration)
//                .build();
        //RedisCacheManager cacheManager=new RedisCacheManager(redisCacheWriter,defaultCacheConfig,initialCacheConfiguration);
        //没有问题
        //也可以设置AutoType的开启，但是据说这个功能之前被爆出了漏洞，现在仍然可能有安全风险,反正实体类都写在entity里了，就这样吧
        return cacheManager;
    }

    //推测下面那玩意没啥卵用
//    /**
//     *  设置 redis 数据默认过期时间
//     *  设置@cacheable 序列化方式
//     * @return
//     */
//    @Bean
//    public RedisCacheConfiguration redisCacheConfiguration(){
//        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
//        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
//        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer)).entryTtl(Duration.ofDays(30));
//        return configuration;
//    }




}