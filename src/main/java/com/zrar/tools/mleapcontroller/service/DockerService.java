package com.zrar.tools.mleapcontroller.service;

import java.io.IOException;

/**
 * @author Jingfeng Zhou
 */
public interface DockerService {

    /**
     * 判断docker服务是否已经正常运行
     * @return
     */
    boolean isStarted();

    /**
     * 重启docker相关服务
     * @throws Exception
     */
    void init();

    /**
     * 复制docker-compose.yml文件
     * @throws Exception
     */
    void copyDockerComposeYml() throws Exception;

    /**
     * 创建docker容器
     * @throws IOException
     */
    void createDocker(String modelName) throws Exception;

    /**
     * 删除docker容器
     * @throws IOException
     */
    void deleteDocker(String modelName) throws Exception;

    /**
     * 根据数据库的记录，重新生成一份docker-compsoe.yml文件
     */
    void generateDockerComposeYml() throws IOException;

    /**
     * 让mleap重新加载一遍模型
     */
    void reloadModels();
}
