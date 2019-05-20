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

    /**
     * 启动时要
     * 1. 创建相关文件夹
     * 2. 删除原有的docker-compose.yml创建的容器
     * 3. 从数据库中恢复docker-compose.yml文件
     * 4. 用新的docker-compose.yml文件创建容器
     * 5. 将模型都加载到容器中
     *
     * @return
     */
    @Bean
    public CommandLineRunner commandLineRunner(CustomConfig customConfig,
                                               Runtime runtime,
                                               @Value("${spring.profiles.active}") String active,
                                               DatabaseService databaseService) {
        return args -> {

            Process process = null;

            // 创建相关文件夹
            File folder = new File(customConfig.getModelOuterPath());
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 删除原有的docker-compose.yml创建的容器
            if (active.equalsIgnoreCase("prod")) {
                process = runtime.exec("docker-compose down");
                log.debug("docker-compose down return {}", process.exitValue());
            }

            // 从数据库中恢复docker-compose.yml文件
            databaseService.generateDockerComposeYml();

            // 用新的docker-compose.yml文件创建容器
            if (active.equalsIgnoreCase("prod")) {
                process = runtime.exec("docker-compose up -d");
                log.debug("docker-compose up -d return {}", process.exitValue());
            }
        };
    }
}
