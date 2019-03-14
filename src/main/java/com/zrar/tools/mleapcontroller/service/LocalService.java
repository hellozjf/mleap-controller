package com.zrar.tools.mleapcontroller.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Jingfeng Zhou
 */
public interface LocalService {

    /**
     * 将附件保存到本地目录
     * @param filename 文件名
     * @param data 文件的数据
     * @return
     */
    boolean saveToLocal(String filename, byte[] data);

    /**
     * 传入一个文件名，返回这个文件在本地临时目录的绝对路径
     * @param filename
     * @return
     */
    String getLocalFilePath(String filename);

    /**
     * 删除所有本地目录下面的临时文件，但是不会删除下面的文件夹
     */
    void clearAll();
}
