package com.zrar.tools.mleapcontroller.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@Data
@Entity
public class MLeapEntity extends BaseEntity {

    /**
     * 模型的名称，例如swModel、yythModel、qgfxModel……
     * 实际的模型文件后面需要加.zip后缀
     * 模型的名称也是后续mleap-bridge需要的模型的路径
     */
    @Column(unique = true)
    private String modelName;

    /**
     * 模型描述
     */
    private String modelDesc;

    /**
     * 自定义字段cutMethod，用哪种方式切词，默认为CutMethodEnum.WORD_CUT.getName()
     */
    private String cutMethod;
}
