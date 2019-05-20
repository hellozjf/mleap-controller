package com.zrar.tools.mleapcontroller.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Data
@Component
@ConfigurationProperties("custom")
public class CustomConfig {

    /**
     * mleap-outter-path，所有数据库、docker-compose.yml、models都在该目录下面
     */
    private String mleapOuterPath;

    /**
     * mleap-bridge的IP地址
     */
    private String bridgeIp;

    /**
     * mleap-bridge的端口
     */
    private String bridgePort;

    /**
     * mleap模型放置在宿主机里面的路径地址
     */
    private String modelOuterPath;

    /**
     * mleap模型放置在docker容器里面的路径地址
     */
    private String modelInnerPath;

    /**
     * docker-compose.yml文件所在的路径地址
     */
    private String dockerComposePath;

    /**
     * harbor的地址，生成docker-compose.yml时需要用到
     */
    private String harborIp;
}
