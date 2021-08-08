package com.jessie.campusmutualassist.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;


@Configuration
@EnableJms
public class ActiveMQConfig {
    //需要给topic定义独立的JmsListenerContainer
    @Bean
    public Queue logQueue() {
        return new ActiveMQQueue("app.log");
    }

}
