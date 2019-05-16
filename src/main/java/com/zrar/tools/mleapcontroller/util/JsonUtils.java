package com.zrar.tools.mleapcontroller.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.dto.Indexes;
import com.zrar.tools.mleapcontroller.exception.MLeapException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class JsonUtils {

    /**
     * 传入分词，返回请求mleap的字符串
     * @param objectMapper
     * @param wordCut
     * @return
     */
    public static String getQueryString(ObjectMapper objectMapper, String wordCut) {
        return getQueryString(objectMapper, Arrays.asList(wordCut));
    }

    /**
     * 传入分词，返回请求mleap的字符串
     * @param objectMapper
     * @param wordCut
     * @param name
     * @return
     */
    public static String getQueryString(ObjectMapper objectMapper, String wordCut, String name) {
        return getQueryString(objectMapper, Arrays.asList(wordCut), name);
    }

    /**
     * 传入分词数组，返回请求mleap的字符串
     * @param objectMapper
     * @param wordCuts
     * @return
     */
    public static String getQueryString(ObjectMapper objectMapper, List<String> wordCuts) {
        return getQueryString(objectMapper, wordCuts, "word");
    }

    /**
     * 传入分词数组，返回请求mleap的字符串
     * @param objectMapper
     * @param wordCuts
     * @param name
     * @return
     */
    public static String getQueryString(ObjectMapper objectMapper, List<String> wordCuts, String name) {
        ObjectNode field = objectMapper.createObjectNode();
        field.put("name", name);
        field.put("type", "string");
        ArrayNode fields = objectMapper.createArrayNode();
        fields.add(field);
        ObjectNode schema = objectMapper.createObjectNode();
        schema.set("fields", fields);
        ArrayNode rows = objectMapper.createArrayNode();
        for (String wordCut : wordCuts) {
            ArrayNode row = objectMapper.createArrayNode();
            row.add(wordCut);
            rows.add(row);
        }
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.set("schema", schema);
        rootNode.set("rows", rows);
        String queryString = null;
        try {
            queryString = objectMapper.writeValueAsString(rootNode);
            log.debug("queryString = {}", queryString);
        } catch (JsonProcessingException e) {
            throw new MLeapException(ResultEnum.JSON_ERROR);
        }
        return queryString;
    }

    /**
     * 传入mleap返回的JSON，返回mlType、predict、probability的坐标
     * @param jsonNode
     * @return
     */
    public static Indexes getIndexes(ObjectMapper objectMapper, JsonNode jsonNode) {

        Indexes indexes = new Indexes();

        JsonNode fields = jsonNode.get("schema").get("fields");
        if (fields.isArray()) {
            ArrayNode arrayNode = (ArrayNode) fields;
            Iterator<JsonNode> jsonNodeIterator = arrayNode.elements();
            int i = 0;
            while (jsonNodeIterator.hasNext()) {
                JsonNode field = jsonNodeIterator.next();
                String name = field.get("name").asText();
                if (name.equalsIgnoreCase("probability")) {
                    indexes.setIndexProbability(i);
                } else if (name.equalsIgnoreCase("predict") || name.equalsIgnoreCase("prediction")) {
                    indexes.setIndexPredict(i);
                } else if (name.equalsIgnoreCase("mlType") || name.equalsIgnoreCase("predictedLabel")) {
                    indexes.setIndexMlType(i);
                }
                i++;
            }
        } else {
            throw new MLeapException(ResultEnum.PREDICT_ERROR);
        }

        return indexes;
    }
}
