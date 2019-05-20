package com.zrar.tools.mleapcontroller.service;

/**
 * @author Jingfeng Zhou
 */
public interface FileService {

    /**
     * 根据模型名称，获取模型所在宿主机的路径
     * @param modelName
     * @return
     */
    String getModelOutterPath(String modelName);

    /**
     * 根据模型名称，获取模型所在docker容器内的路径
     * @param modelName
     * @return
     */
    String getModelInnerPath(String modelName);

    /**
     * 获取docker-compose.yml文件的路径
     * @return
     */
    String getDockerComposeYmlPath();
}
