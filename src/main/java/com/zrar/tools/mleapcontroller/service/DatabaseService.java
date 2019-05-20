package com.zrar.tools.mleapcontroller.service;

import java.io.IOException;

/**
 * @author Jingfeng Zhou
 */
public interface DatabaseService {

    void init() throws Exception;

    /**
     * 根据数据库的记录，重新生成一份docker-compsoe.yml文件
     */
    void generateDockerComposeYml() throws IOException;

    /**
     * 让mleap重新加载一遍模型
     */
    void uploadModels();
}
