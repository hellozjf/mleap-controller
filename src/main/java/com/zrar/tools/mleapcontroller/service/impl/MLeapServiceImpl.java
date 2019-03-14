package com.zrar.tools.mleapcontroller.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zrar.tools.mleapcontroller.config.MLeapConfig;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class MLeapServiceImpl implements MLeapService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MLeapConfig mLeapConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String onlineModel(String fileName) {
        try {
            // 获取模型上线的URL
            String url = mLeapConfig.getModelUrl();
            // 模型的位置
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("path", mLeapConfig.getFolder() + fileName);
            String requestBody = objectMapper.writeValueAsString(objectNode);
            // 构造PUT请求，上线模型
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            // 返回上线的结果
            String responseBody = response.getBody();
            return responseBody;
        } catch (JsonProcessingException e) {
            log.error("e = {}", e);
            return null;
        }
    }

    @Override
    public String invokeModel(String data) {
        // 获取模型预测的URL
        String url = mLeapConfig.getTransformUrl();
        // 获取待预测的数据
        String requestBody = data;
        // 发送POST请求，预测数据
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        // 返回预测结果
        String responseBody = response.getBody();
        return responseBody;
    }

    @Override
    public String offlineModel() {
        // 获取模型上线的URL
        String url = mLeapConfig.getModelUrl();
        // 发送DELETE请求，删除模型
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
        // 返回删除的结果
        return response.getBody();
    }
}
