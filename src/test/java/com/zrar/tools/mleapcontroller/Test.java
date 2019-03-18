package com.zrar.tools.mleapcontroller;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
public class Test {

    @org.junit.Test
    public void test() {
        String wordcut = wordCut("个人代开劳务发票的四千、八百是怎么回事？", "vswzyc");
        log.debug("{}", wordcut);
    }

    /**
     *  切词方法，仅保留税务专有词
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
            log.debug("i={}", i);
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
