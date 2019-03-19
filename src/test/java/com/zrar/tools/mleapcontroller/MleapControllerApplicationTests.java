package com.zrar.tools.mleapcontroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrar.tools.mleapcontroller.util.WordUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MleapControllerApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void contextLoads() {
    }

    /**
     * 测试上线模型
     */
    @Test
    public void testOnlineModel() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://aliyun.hellozjf.com:8080/mleap1/onlineModel";
        Resource resource = new ClassPathResource("swModel.zip");
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
        String url = "http://aliyun.hellozjf.com:8080/mleap1/offlineModel";
        String result = restTemplate.postForObject(url, null, String.class);
        log.debug("result = {}", result);
    }

    /**
     * 测试调用模型
     * 需要导入airbnb.model.lr.zip或airbnb.model.rf.zip模型
     */
    @Test
    public void testInvokeModel() throws Exception {

        String url = "http://aliyun.hellozjf.com:8080/mleap1/invokeModel";

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
        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
    }

    /**
     * 测试切词方法
     */
    @Test
    public void testWordCut() {
        String line = "劳务报酬所得如何缴税？";
        String result = WordUtils.wordCut(line, WordUtils.SWZYC);
        log.debug("result = {}", result);
    }

    /**
     * 测试税务专有词预测
     * 需要导入swModel.zip模型
     * @throws Exception
     */
    @Test
    public void testPredict() throws Exception {
        String url = "http://aliyun.hellozjf.com:8080/mleap1/predict";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("新个税继续教育专项附加扣除中，扣除范围是怎么规定的？", requestHeaders);

        String result = restTemplate.postForObject(url, requestEntity, String.class);
        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
    }

    /**
     * 测试税务专有词预测
     * 需要导入swModel.zip模型
     * @throws Exception
     */
    @Test
    public void testPredict2() throws Exception {
        String url = "http://aliyun.hellozjf.com:8080/mleap1/predict2";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        String body = "自然人税收管理系统扣缴客户端，导出的劳务报酬所得，一般劳务报酬所得填写什么？\n" +
                "个税工资计算？\n" +
                "1.个体是否需要报税2.什么时间开始报税\n" +
                "住房租金扣除问题？\n" +
                "2019年全年一次性奖金个人所得税如何计算缴纳？\n" +
                "【个人所得税税收政策的热点问题】男女朋友共同购买的房屋，都是共同还款人，房产证是女方的名字，住房贷款利息，男方能扣吗？";
        HttpEntity<String> requestEntity = new HttpEntity<>(body, requestHeaders);

        String result = restTemplate.postForObject(url, requestEntity, String.class);
        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
    }
}
