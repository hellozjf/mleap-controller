package com.zrar.tools.mleapcontroller.service;

import com.zrar.tools.mleapcontroller.vo.CutMethodVO;
import com.zrar.tools.mleapcontroller.vo.MLeapFileVO;
import com.zrar.tools.mleapcontroller.vo.ModelVO;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
public interface WebService {

    /**
     * 从缓存中获取modelName列表，通过updateModelNameList更新modelName列表
     * @return
     */
    List<String> getModelNameList();

    /**
     * 从docker-compose.yml中更新modelName列表
     * @return
     */
    List<String> updateModelNameList();

    /**
     * 获取切词方式列表，结果需要缓存
     * @return
     */
    List<CutMethodVO> getCutMethodVOList();

    /**
     *
     * @return
     */
    List<ModelVO> getModelVOList();

    /**
     * 获取所有MLeap清单，供前端展示使用
     * @return
     */
    List<MLeapFileVO> getAllMLeapFileVOList();
}
