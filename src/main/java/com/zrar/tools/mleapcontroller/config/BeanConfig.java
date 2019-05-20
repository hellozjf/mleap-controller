package com.zrar.tools.mleapcontroller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

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

    /**
     * 在此读取数据库中已有的模型数据，并将其加载
     * @return
     */
    @Bean
    public CommandLineRunner commandLineRunner(MleapConfig mleapConfig) {
        return args -> {
            // 启动的时候要确保相关文件夹都存在
            File folder = new File(mleapConfig.getModelOutterPath());
            if (! folder.exists()) {
                folder.mkdirs();
            }
        };
    }
}
