package com.zrar.tools.mleapcontroller.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jingfeng Zhou
 */
@Data
public class ModelVO implements Serializable {
    private String id;
    private String modelName;
    private String modelDesc;
    private String modelMd5;
    private String modelCutMethodName;
    private String modelCutMethodDesc;
}
