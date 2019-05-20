package com.zrar.tools.mleapcontroller.service;

import com.zrar.tools.mleapcontroller.vo.TaxClassifyPredictVO;

import java.io.File;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
public interface MLeapService {

    /**
     * 上线模型
     * @param modelName
     * @return
     */
    String online(String modelName);

    /**
     * 下线模型
     * @param modelName
     * @return
     * @throws Exception
     */
    String offline(String modelName);

    /**
     * 调用某个mleap服务的transform接口，传输的内容是data，返回的是一个String
     * @param modelName
     * @param data
     * @return
     */
    String transform(String modelName, String data);

    /**
     * 调用某个mleap服务的预测接口，传输的是待预测的字符串，返回的是预测结果
     * @param modelName
     * @param raw
     * @param nature 词性，如果为null表示不筛选词性
     * @return
     */
    TaxClassifyPredictVO predict(String modelName, String raw, String nature);

    /**
     * 调用某个mleap服务的预测接口，传输的是待预测的字符串列表，返回的是预测结果列表
     * @param modelName
     * @param raws
     * @param nature 词性，如果为null表示不筛选词性
     * @return
     */
    List<TaxClassifyPredictVO> predict(String modelName, List<String> raws, String nature);

}
