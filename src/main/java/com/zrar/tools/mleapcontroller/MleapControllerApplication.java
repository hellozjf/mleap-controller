package com.zrar.tools.mleapcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class MleapControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MleapControllerApplication.class, args);
    }

}
