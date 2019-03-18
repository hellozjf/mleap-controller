package com.zrar.tools.mleapcontroller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.util.ResultUtils;
import com.zrar.tools.mleapcontroller.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@RestController
@Slf4j
public class MLeapController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 接收一个模型文件，让它上线
     * @param multipartFile
     * @return
     */
    @PostMapping("/{mleap}/onlineModel")
    public ResultVO onlineModel(@PathVariable("mleap") String mleap,
                                @RequestParam("file") MultipartFile multipartFile) {

        // 判断上传过来的文件是不是空的
        if (multipartFile.isEmpty()) {
            return ResultUtils.error(ResultEnum.FILE_CAN_NOT_BE_EMPTY);
        }

        // 获取文件名
        String filename = multipartFile.getOriginalFilename();
        File folder = new File("/models");
        File file = new File(folder, filename);

        // 将上传上来的文件保存到/models目录
        try {
            byte[] data = multipartFile.getBytes();
            // 如果待保存的文件夹不存在，那就创建一个文件夹
            if (! folder.exists()) {
                folder.mkdirs();
            }
            // 然后把文件保存下来
            IOUtils.copy(new ByteArrayInputStream(data), new FileOutputStream(file));
        } catch (IOException e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.FILE_IS_WRONG);
        }

        // 让模型上线
        try {
            // 获取模型上线的URL
            String url = "http://" + mleap + ":65327/model";
            // 模型的位置
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("path", file.getAbsolutePath());
            String requestBody = objectMapper.writeValueAsString(objectNode);
            // 构造PUT请求，上线模型
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            // 返回上线的结果
            String responseBody = response.getBody();
            return ResultUtils.success(responseBody);
        } catch (JsonProcessingException e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.MODEL_ONLINE_FAILED);
        }
    }

    /**
     * 让模型下线
     * @return
     */
    @PostMapping("/{mleap}/offlineModel")
    public ResultVO offlineModel(@PathVariable("mleap") String mleap) {
        // 获取模型上线的URL
        String url = "http://" + mleap + ":65327/model";
        // 发送DELETE请求，删除模型
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
        // 返回删除的结果
        return ResultUtils.success(response.getBody());
    }

    /**
     * 调用模型，返回预测结果
     * @param data
     * @return
     */
    @PostMapping("/{mleap}/invokeModel")
    public ResultVO invokeModel(@PathVariable("mleap") String mleap,
                                @RequestBody String data) {
        try {
            // 获取模型预测的URL
            String url = "http://" + mleap + ":65327/transform";
            // 获取待预测的数据
            String requestBody = data;
            // 发送POST请求，预测数据
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            // 返回预测结果
            String result = response.getBody();
            JsonNode objectNode = objectMapper.readTree(result);
            return ResultUtils.success(objectNode);
        } catch (IOException e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.INVOKE_FAILED);
        }
    }


}
