package com.zrar.tools.mleapcontroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SwModelTest {

    @Autowired
    private ObjectMapper objectMapper;

    private String site = "http://aliyun.hellozjf.com:8080";
    private String filename = "swModel.zip";
    private String mleap = "mleap1";

    /**
     * 测试上线模型
     */
    @Test
    public void testSwModel() throws Exception {

        // 首先上传模型
        uploadSwModel();

        // 然后进行预测
        predictSingleSwModel();
        predictMultiSwModel();
    }

    @Test
    public void uploadSwModel() throws Exception {

        ToolUtils.upload(site, mleap, filename, objectMapper);
    }

    @Test
    public void predictSingleSwModel() throws Exception {

        // 税务专有词里面有词性，所以需要加nature=vswzyc
        String url = site + "/" + mleap + "/predict?nature=vswzyc";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("新个税继续教育专项附加扣除中，扣除范围是怎么规定的？", requestHeaders);

        String result = restTemplate.postForObject(url, requestEntity, String.class);
        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        Assert.assertEquals(jsonNode.get("code").intValue(), 0);
        Assert.assertNotNull(jsonNode.get("data").get("predictString").textValue());
        Assert.assertNotNull(jsonNode.get("data").get("predict").intValue());
    }

    @Test
    public void predictMultiSwModel() throws Exception {

        // 税务专有词里面有词性，所以需要加nature=vswzyc
        String url = site + "/" + mleap + "/predict2?nature=vswzyc";

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
        Assert.assertEquals(jsonNode.get("code").intValue(), 0);
    }
}
