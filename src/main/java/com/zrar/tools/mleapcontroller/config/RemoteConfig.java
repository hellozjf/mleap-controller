package com.zrar.tools.mleapcontroller.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Data
@Component
@ConfigurationProperties("remote")
public class RemoteConfig {
    private String address;
    private String username;
    private String password;
    private String folder;
}
