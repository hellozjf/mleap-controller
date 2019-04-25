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
     * 例如mleap1、mleap2……
     */
    private String mleapName;

    /**
     * 例如qgfxModel.zip、yythModel.zip……
     */
    private String modelPath;
}
