package com.zrar.tools.mleapcontroller.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

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
     * @param saveNature 词性，如果为null表示不筛选词性
     */
    public static String wordCut(Object lineObject,Object saveNature) {
        if (lineObject == null) {
            return "null";
        }
        String line = lineObject.toString().toUpperCase();
        List<Term> termList = HanLP.segment(line);
        StringBuffer res = new StringBuffer();
        for (Term i : termList) {
            // 这里可以打印i看看有哪些词性
            String word = i.word;
            String nature = i.nature.toString();
            // 如果saveNature==null或空字符串，表示不筛选词性，直接加进来
            // 如果saveNature!=null，表示需要筛选词性，要判断这是不是我需要的词性，是才加进来
            if (StringUtils.isEmpty(saveNature) || nature.contains(saveNature.toString())) {
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

    /**
     * 切短语
     * HanLP提取关键短语
     * @author bigdata-陈晓曦
     */
    public static String phraseList(Object lineObject){
        if(lineObject==null){
            return "null";
        }
        String line = lineObject.toString();
        List<String> phraseList = HanLP.extractPhrase(line, 10);
        StringBuffer res = new StringBuffer("");
        for(String i:phraseList){
            res.append(i).append(" ");
        }
        if(res.length()>0){
            return res.substring(0,res.length()-1);
        }else {
            return "null";
        }
    }
}
