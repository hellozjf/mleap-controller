package com.zrar.tools.mleapcontroller.service;

/**
 * @author Jingfeng Zhou
 */
public interface RemoteService {

    /**
     * 将本地文件通过scp的方式上传到远端服务器上面
     * @param absoluteFilePath
     * @return
     */
    boolean scp(String absoluteFilePath);
}
