package com.zrar.tools.mleapcontroller.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * @author Jingfeng Zhou
 */
public interface DatabaseService {

    /**
     * 根据数据库的记录，重新生成一份docker-compsoe.yml文件
     */
    void generateDockerComposeYml() throws IOException;
}
