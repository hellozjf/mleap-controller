package com.zrar.tools.mleapcontroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class YythModelTest {

    @Autowired
    private ObjectMapper objectMapper;

    private String site = "http://aliyun.hellozjf.com:8080";
    private String filename = "yythModel.zip";
    private String mleap = "mleap2";

    /**
     * 测试上线模型
     */
    @Test
    public void testYythModel() throws Exception {

        // 首先上传模型
        uploadYythModel();

        // 然后进行预测
        predictSingleYythModel();
        predictMultiYythModel();
    }

    private void uploadYythModel() throws Exception {

        ToolUtils.upload(site, mleap, filename, objectMapper);
    }

    private void predictSingleYythModel() throws Exception {

        String url = site + "/" + mleap + "/predict";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("你看税务税务等，嗯，那个你要那个的话你打12366转投诉你跟他们说，跟我们联系，你这现在你跟我联系的是咨询没用的，你要跟他说投诉投诉工单，他就给我那个咨询了给我，那你就跟他说我来投诉你，不要咨询给我转咨询，但是你说这不要转咨询转投诉噢", requestHeaders);

        String result = restTemplate.postForObject(url, requestEntity, String.class);
        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        Assert.assertEquals(jsonNode.get("code").intValue(), 0);
    }

    private void predictMultiYythModel() throws Exception {

        String url = site + "/" + mleap + "/predict2";

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
