package com.jessie.campusmutualassist;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jessie.campusmutualassist.mapper")
public class CampusMutualAssistApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusMutualAssistApplication.class, args);
    }

}
