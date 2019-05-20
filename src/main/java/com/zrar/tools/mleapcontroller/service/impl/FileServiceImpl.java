package com.zrar.tools.mleapcontroller.service.impl;

import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private CustomConfig customConfig;

    /**
     * 根据模型名称，获取模型所在宿主机的路径
     * @param modelName
     * @return
     */
    @Override
    public String getModelOutterPath(String modelName) {
        return customConfig.getModelOuterPath() + "/" + modelName + ".zip";
    }

    /**
     * 根据模型名称，获取模型所在docker容器内的路径
     * @param modelName
     * @return
     */
    @Override
    public String getModelInnerPath(String modelName) {
        return customConfig.getModelInnerPath() + "/" + modelName + ".zip";
    }

    @Override
    public String getDockerComposeYmlPath() {
        return customConfig.getDockerComposePath();
    }
}
