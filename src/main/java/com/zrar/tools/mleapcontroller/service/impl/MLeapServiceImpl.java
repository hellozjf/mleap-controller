package com.zrar.tools.mleapcontroller.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zrar.tools.mleapcontroller.config.MleapConfig;
import com.zrar.tools.mleapcontroller.constant.CutMethodEnum;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.dto.Indexes;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.exception.MLeapException;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import com.zrar.tools.mleapcontroller.service.NetworkService;
import com.zrar.tools.mleapcontroller.util.JsonUtils;
import com.zrar.tools.mleapcontroller.util.WordUtils;
import com.zrar.tools.mleapcontroller.vo.TaxClassifyPredictVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class MLeapServiceImpl implements MLeapService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private MLeapRepository mLeapRepository;

    @Autowired
    private MleapConfig mleapConfig;

    @Override
    public String online(String mleap, File file) {
        // 获取模型上线的URL
        String url = getOnlineUrl(mleap);
        // 模型的位置
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("path", mleapConfig.getModelInnerPath() + "/" + file.getName());
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            log.error("e = {}", e);
            throw new MLeapException(ResultEnum.JSON_ERROR);
        }
        // 构造PUT请求，上线模型
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        // 返回上线的结果
        String responseBody = response.getBody();
        return responseBody;
    }

    @Override
    public String offline(String mleap) {
        // 获取模型上线的URL
        String url = getOfflineUrl(mleap);
        // 发送DELETE请求，删除模型
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
        // 返回删除的结果
        return response.getBody();
    }

    @Override
    public String transform(String mleap, String data) {
        // 获取模型预测的URL
        String url = getTransformUrl(mleap);
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
        return result;
    }

    @Override
    public TaxClassifyPredictVO predict(String mleap, String raw, String nature) {
        List<TaxClassifyPredictVO> taxClassifyPredictVOList = predict(mleap, Arrays.asList(raw), nature);
        return taxClassifyPredictVOList.get(0);
    }

    @Override
    public List<TaxClassifyPredictVO> predict(String mleap, List<String> raws, String nature) {

        MLeapEntity mLeapEntity = mLeapRepository.findByMleapName(mleap);

        // 切词
        List<String> wordCuts = new ArrayList<>();
        for (String raw : raws) {
            String wordCut = null;
            if (CutMethodEnum.WORD_CUT.getName().equalsIgnoreCase(mLeapEntity.getCutMethod())) {
                log.debug("choose wordCut");
                wordCut = WordUtils.wordCut(raw, nature);
            } else if (CutMethodEnum.WORD_CUT_VSWZYC.getName().equalsIgnoreCase(mLeapEntity.getCutMethod())) {
                log.debug("choose wordCut vswzyc");
                wordCut = WordUtils.wordCut(raw, CutMethodEnum.WORD_CUT_VSWZYC.getNature());
            } else if (CutMethodEnum.PHRASE_LIST.getName().equalsIgnoreCase(mLeapEntity.getCutMethod())) {
                log.debug("choose phraseList");
                wordCut = WordUtils.phraseList(raw);
            } else {
                log.error("未知切词方式：{}", wordCut);
                // 将切词方式更新为wordCut
                mLeapEntity.setCutMethod(CutMethodEnum.WORD_CUT.getName());
                mLeapRepository.save(mLeapEntity);
                wordCut = WordUtils.wordCut(raw, nature);
            }
            wordCuts.add(wordCut);
        }

        // 构造请求体
        String queryString = JsonUtils.getQueryString(objectMapper, wordCuts);
        log.debug("queryString = {}", queryString);

        // 预测模型
        String result = transform(mleap, queryString);
        log.debug("transform result = {}", result);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(result);
        } catch (IOException e) {
            throw new MLeapException(ResultEnum.JSON_ERROR);
        }

        // 获取mlType和probability的index
        Indexes indexes = JsonUtils.getIndexes(objectMapper, jsonNode);
        if (indexes == null || indexes.getIndexPredict() == null || indexes.getIndexProbability() == null) {
            // 如果概率，预测项目为空，返回预测错误
            // 注意预测字符串可能为空，因为晓曦的模型返回的mlType确实不存在
            throw new MLeapException(ResultEnum.PREDICT_ERROR);
        }
        log.debug("indexMlType={}, indexPredict={}, indexProbability={}",
                indexes.getIndexMlType(), indexes.getIndexPredict(), indexes.getIndexProbability());

        // 取出预测字符串和预测的概率
        List<TaxClassifyPredictVO> taxClassifyPredictVOList = new ArrayList<>();
        JsonNode rows = jsonNode.get("rows");
        if (rows.isArray()) {
            int i = 0;
            ArrayNode arrayNode = (ArrayNode) rows;
            Iterator<JsonNode> iterator = arrayNode.elements();
            while (iterator.hasNext()) {
                JsonNode row = iterator.next();

                Integer predict = null;
                String predictString = null;
                Double predictProbability = null;
                if (row.isArray()) {
                    arrayNode = (ArrayNode) row;
                    if (indexes.getIndexMlType() != null) {
                        predictString = arrayNode.get(indexes.getIndexMlType()).asText();
                    }
                    log.debug("values = {}", arrayNode.get(indexes.getIndexProbability()).get("values"));
                    predict = arrayNode.get(indexes.getIndexPredict()).intValue();
                    predictProbability = arrayNode.get(indexes.getIndexProbability()).get("values").get(predict).asDouble();
                    // 判断是否有错误
                    if (predictProbability == null) {
                        // 如果取不到预测的概率，返回预测错误
                        // 注意，字符串确实有可能取不到
                        throw new MLeapException(ResultEnum.PREDICT_ERROR);
                    }
                }

                TaxClassifyPredictVO taxClassifyPredictVO = new TaxClassifyPredictVO();
                taxClassifyPredictVO.setRaw(raws.get(i));
                taxClassifyPredictVO.setWord(wordCuts.get(i));
                taxClassifyPredictVO.setPredict(predict);
                taxClassifyPredictVO.setPredictString(predictString);
                taxClassifyPredictVO.setProbability(predictProbability);
                taxClassifyPredictVOList.add(taxClassifyPredictVO);

                i++;
            }
        }

        // 返回预测结果
        log.debug("taxClassifyPredictVOList = {}", taxClassifyPredictVOList);
        return taxClassifyPredictVOList;
    }

    private String getOnlineUrl(String mleap) {
        return getModelUrl(mleap);
    }

    private String getOfflineUrl(String mleap) {
        return getModelUrl(mleap);
    }

    private String getModelUrl(String mleap) {
        return getUrl(mleap, "model");
    }

    private String getTransformUrl(String mleap) {
        return getUrl(mleap, "transform");
    }

    private String getUrl(String mleap, String type) {
        // 构造mleap-bridge能够接收的地址
        String url = "http://" + mleapConfig.getBridgeIp() + ":" + mleapConfig.getBridgePort() + "/" + mleap + "/" + type;
        return url;
    }
}
