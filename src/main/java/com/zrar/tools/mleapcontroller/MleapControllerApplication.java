package com.zrar.tools.mleapcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableTransactionManagement
public class MleapControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MleapControllerApplication.class, args);
    }

}
