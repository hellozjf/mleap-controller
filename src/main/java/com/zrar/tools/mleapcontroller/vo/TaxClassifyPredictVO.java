package com.zrar.tools.mleapcontroller.vo;

import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
public class TaxClassifyPredictVO {

    /**
     * 原始字符串
     */
    private String raw;

    /**
     * 分词
     */
    private String word;

    /**
     * 预测的分类结果
     */
    private String predict;

    /**
     * 预测的分类概率
     */
    private Double probability;

}
