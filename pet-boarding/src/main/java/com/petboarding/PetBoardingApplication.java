package com.petboarding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.petboarding.modules.**.mapper")
@EnableCaching
@EnableScheduling
public class PetBoardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetBoardingApplication.class, args);
    }
}
