package com.zrar.tools.mleapcontroller.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@Data
@Entity
public class MLeapEntity extends BaseEntity {

    /**
     * 模型的名称
     * 例如mleap1、mleap2……
     */
    private String mleapName;

    /**
     * 模型的路径
     * 例如qgfxModel.zip、yythModel.zip……
     */
    private String modelPath;

    /**
     * 模型的大小
     */
    private Long size;

    /**
     * 模型描述
     */
    private String desc;

    /**
     * 自定义字段cutMethod，用哪种方式切词，默认为CutMethodEnum.WORD_CUT.getName()
     */
    private String cutMethod;
}
