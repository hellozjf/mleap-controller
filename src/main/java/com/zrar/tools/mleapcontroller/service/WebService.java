package com.zrar.tools.mleapcontroller.service;

import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.vo.CutMethodVO;
import com.zrar.tools.mleapcontroller.vo.ModelVO;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
public interface WebService {

    /**
     * 获取切词方式列表，结果需要缓存
     * @return
     */
    List<CutMethodVO> getCutMethodVOList();

    /**
     * 获取所有模型列表
     * @return
     */
    List<ModelVO> getModelVOList();

    /**
     * 添加模型
     * @param modelName
     * @param modelDesc
     * @param cutMethodName
     * @return
     */
    MLeapEntity addModel(String modelName,
                         String modelDesc,
                         String cutMethodName) throws Exception;

    /**
     * 删除模型
     * @param modelName
     * @return
     */
    void delModel(String modelName) throws Exception;

    /**
     * 更新模型
     * @param modelName
     * @param modelDesc
     * @param cutMethodName
     * @return
     */
    MLeapEntity updateModel(String modelName,
                            String modelDesc,
                            String cutMethodName);
}
