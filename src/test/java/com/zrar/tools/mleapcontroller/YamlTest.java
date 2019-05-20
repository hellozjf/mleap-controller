package com.zrar.tools.mleapcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class YamlTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("yamlObjectMapper")
    private ObjectMapper yamlObjectMapper;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private MLeapRepository mLeapRepository;

    /**
     * 打印docker-compose.yml文件看看
     */
    @Test
    public void printYaml() throws Exception {
        Map map = yamlObjectMapper.readValue(new ClassPathResource("docker-compose.yml").getInputStream(), Map.class);
        log.debug("map = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
        Map<String, Object> services = (Map<String, Object>) map.get("services");
        for (String key : services.keySet()) {
            log.debug("key = {}", key);
        }
    }

}
