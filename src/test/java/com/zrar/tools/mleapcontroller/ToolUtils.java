package com.zrar.tools.mleapcontroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class ToolUtils {

    public static void upload(String site, String mleap, String filename, ObjectMapper objectMapper) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        String url = site + "/" + mleap + "/onlineModel";
        Resource resource = new ClassPathResource(filename);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);

        String result = restTemplate.postForObject(url, param, String.class);
        log.debug("result = {}", result);
        JsonNode jsonNode = objectMapper.readTree(result);
        Assert.assertEquals(jsonNode.get("code").intValue(), 0);
    }
}
