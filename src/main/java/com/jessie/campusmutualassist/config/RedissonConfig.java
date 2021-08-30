package com.jessie.campusmutualassist.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;
    // 配置文件中你自己的redis端口号
    @Value("${spring.redis.port}")
    private String port;
    // 配置文件中你自己的redis密码

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port)
                .setConnectionMinimumIdleSize(10)
                .setSubscriptionConnectionPoolSize(64)
        ;
        //添加主从配置
        return Redisson.create(config);
    }


}
