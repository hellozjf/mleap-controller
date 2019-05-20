package com.zrar.tools.mleapcontroller.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Data
@Component
@ConfigurationProperties("mleap")
public class MleapConfig {

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
    private String modelOutterPath;

    /**
     * mleap模型放置在docker容器里面的路径地址
     */
    private String modelInnerPath;
}
