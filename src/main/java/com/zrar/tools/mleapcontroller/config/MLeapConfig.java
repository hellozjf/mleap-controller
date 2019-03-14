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
public class MLeapConfig {
    private String modelUrl;
    private String transformUrl;
    private String folder;
}
