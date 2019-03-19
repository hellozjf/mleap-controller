package com.zrar.tools.mleapcontroller.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class WordUtils {

    /**
     * 词性：税务专有词
     */
    public static final String SWZYC = "vswzyc";

    /**
     * 切词方法，仅保留税务专有词
     * @param lineObject 需要分词的句子
     * @param saveNature 词性，vswzyc是税务专有词
     */
    public static String wordCut (Object lineObject,Object saveNature) {
        if (lineObject == null) {
            return "null";
        }
        if (saveNature == null) {
            return "null";
        }
        String line = lineObject.toString().toUpperCase();
        List<Term> termList = HanLP.segment(line);
        StringBuffer res = new StringBuffer();
        for (Term i : termList) {
            // 这里可以打印i看看有哪些词性
            String word = i.word;
            String nature = i.nature.toString();
            if (nature.contains(saveNature.toString())) {
                //词性为vswzyc（即税务专有词）的分词结果保留
                res.append(word).append(" ");
            }
        }
        if (res.length() > 0) {
            return res.substring(0, res.length() - 1);
        } else {
            return "null";
        }
    }
}
