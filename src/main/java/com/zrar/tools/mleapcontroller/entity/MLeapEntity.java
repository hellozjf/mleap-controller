package com.zrar.tools.mleapcontroller.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@Data
public class MLeapEntity extends BaseEntity {

    /**
     * 例如mleap1、mleap2……
     */
    private String mleapName;

    /**
     * 例如/models/qgfxModel.zip、/models/yythModel.zip……
     */
    private String modelPath;
}
