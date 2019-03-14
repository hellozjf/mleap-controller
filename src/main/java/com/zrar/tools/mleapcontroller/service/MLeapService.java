package com.zrar.tools.mleapcontroller.service;

/**
 * @author Jingfeng Zhou
 */
public interface MLeapService {

    /**
     * 上线模型
     *
     * @param fileName 模型文件名
     * @return 上线模型的JSON返回结果
     */
    String onlineModel(String fileName);

    /**
     * 调用模型
     *
     * @param data 测试数据
     * @return 测试数据的预测结果
     */
    String invokeModel(String data);

    /**
     * 下线模型
     * @return
     */
    String offlineModel();
}
