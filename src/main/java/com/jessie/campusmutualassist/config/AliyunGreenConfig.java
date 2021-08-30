package com.jessie.campusmutualassist.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunGreenConfig {
    @Value("${aliyun.accessKeyId}")
    String ALIYUN_ACCESS_KEY_ID;
    @Value("${aliyun.accessSecret}")
    String ALIYUN_ACCESS_KEY_SECRET;
    @Value("${aliyun.regionID}")
    String REGION_ID;

    @Bean
    public IAcsClient iAcsClient() {
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID, ALIYUN_ACCESS_KEY_ID, ALIYUN_ACCESS_KEY_SECRET);
        return new DefaultAcsClient(profile);
    }
}
