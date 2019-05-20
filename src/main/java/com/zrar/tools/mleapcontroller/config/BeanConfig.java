package com.zrar.tools.mleapcontroller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.zrar.tools.mleapcontroller.service.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * 存放系统运行所需要的bean
 *
 * @author Jingfeng Zhou
 */
@Configuration
@Slf4j
public class BeanConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean("yamlObjectMapper")
    public ObjectMapper yamlObjectMapper() {
        ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());
        return yamlObjectMapper;
    }

    @Bean
    public Runtime runtime() {
        return Runtime.getRuntime();
    }

    @Bean
    public CommandLineRunner commandLineRunner(DatabaseService databaseService) {
        return args -> {
            try {
                databaseService.init();
            } catch (Exception e) {
                log.error("e = {}", e);
                System.exit(-1);
            }
        };
    }
}
