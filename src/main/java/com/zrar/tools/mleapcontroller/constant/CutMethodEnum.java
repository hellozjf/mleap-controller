package com.zrar.tools.mleapcontroller.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@Getter
@AllArgsConstructor
public enum CutMethodEnum {

    WORD_CUT("wordCut", "切词", ""),
    WORD_CUT_VSWZYC("wordCut_vswzyc", "切词——税务专有词", "vswzyc"),
    PHRASE_LIST("phraseList", "切短语", ""),
    ;

    String name;
    String desc;
    String nature;
}
