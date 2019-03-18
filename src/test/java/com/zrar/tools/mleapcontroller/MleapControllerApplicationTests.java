package com.zrar.tools.mleapcontroller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MleapControllerApplicationTests {

    @Test
    public void contextLoads() {
    }

    /**
     * 测试上线模型
     */
    @Test
    public void testOnlineModel() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://192.168.2.150:8080/mleap1/onlineModel";
        Resource resource = new ClassPathResource("airbnb.model.lr.zip");
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        String result = restTemplate.postForObject(url, param, String.class);
        log.debug("result = {}", result);
    }

    /**
     * 测试下线模型
     */
    @Test
    public void testOfflineModel() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://192.168.2.150:8080/mleap1/offlineModel";
        String result = restTemplate.postForObject(url, null, String.class);
        log.debug("result = {}", result);
    }

    /**
     * 测试调用模型
     */
    @Test
    public void testInvokeModel() {

        String url = "http://192.168.2.150:8080/mleap1/invokeModel";

        // 读取文件
        Resource resource = new ClassPathResource("frame.airbnb.json");
        StringBuilder stringBuilder = new StringBuilder();
        try (
                InputStream inputStream = resource.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }
        } catch (Exception e) {
            log.error("e = {}", e);
            Assert.fail();
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(stringBuilder.toString(), requestHeaders);

        String result = restTemplate.postForObject(url, requestEntity, String.class);
        log.debug("result = {}", result);
    }
}
