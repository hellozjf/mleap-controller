package com.zrar.tools.mleapcontroller.service;

import java.util.List;

/**
 * @author Jingfeng Zhou
 *
 * 用于判断某个mleap服务是否可用
 *
 */
public interface NetworkService {

    /**
     * 判断某个mleap地址是否可以连接
     * @param address
     * @return
     */
    boolean isReachable(String address);

    /**
     * 获取可用的mleap列表
     * @return
     */
    List<String> getReachableMleapList();
}
