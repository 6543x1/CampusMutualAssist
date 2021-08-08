package com.jessie.campusmutualassist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CampusMutualAssistApplication.class)
public class SpringBootProducer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Test
    public void ptSender(){
        jmsMessagingTemplate.convertAndSend("defaultTestQueue","HelloActiveMQ By pool");
    }

}
