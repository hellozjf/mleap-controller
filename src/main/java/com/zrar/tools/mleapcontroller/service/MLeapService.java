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
     * @param mleap
     * @param file
     * @return
     */
    String online(String mleap, File file);

    /**
     * 下线模型
     * @param mleap
     * @return
     * @throws Exception
     */
    String offline(String mleap);

    /**
     * 调用某个mleap服务的transform接口，传输的内容是data，返回的是一个String
     * @param mleap
     * @param data
     * @return
     */
    String transform(String mleap, String data);

    /**
     * 调用某个mleap服务的预测接口，传输的是待预测的字符串，返回的是预测结果
     * @param mleap
     * @param raw
     * @param nature 词性，如果为null表示不筛选词性
     * @return
     */
    TaxClassifyPredictVO predict(String mleap, String raw, String nature);

    /**
     * 调用某个mleap服务的预测接口，传输的是待预测的字符串列表，返回的是预测结果列表
     * @param mleap
     * @param raws
     * @param nature 词性，如果为null表示不筛选词性
     * @return
     */
    List<TaxClassifyPredictVO> predict(String mleap, List<String> raws, String nature);
}
