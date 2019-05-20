package com.zrar.tools.mleapcontroller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Data
public class DockerComposeDTO {
    private String version;
    private JsonNode services;
    private Networks networks;

    @Data
    public class Service {
        private String image;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> ports;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> volumes;
        private List<String> networks;
    }

    @Data
    public class Networks {
        @JsonProperty("mleap-bridge")
        private MleapBridge mleapBridge;

        @Data
        public class MleapBridge {
            private String driver;
            private Ipam ipam;

            @Data
            public class Ipam {
                private String driver;
                private List<Config> config;

                @Data
                public class Config {
                    private String subnet;
                }
            }
        }
    }
}
