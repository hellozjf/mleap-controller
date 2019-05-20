package com.zrar.tools.mleapcontroller.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.dto.DockerComposeDTO;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Autowired
    private MLeapRepository mLeapRepository;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    @Qualifier("yamlObjectMapper")
    private ObjectMapper yamlObjectMapper;

    @Override
    public void generateDockerComposeYml() throws IOException {

        DockerComposeDTO dockerComposeDTO = new DockerComposeDTO();

        // 配置version
        dockerComposeDTO.setVersion("3");

        // 配置services
        ObjectNode services = yamlObjectMapper.createObjectNode();

        // 添加各个模型的service
        List<MLeapEntity> mLeapEntityList = mLeapRepository.findAll();
        for (MLeapEntity mLeapEntity : mLeapEntityList) {
            DockerComposeDTO.Service service = dockerComposeDTO.new Service();
            service.setImage(customConfig.getHarborIp() + "/zrar/mleap-serving:0.9.0-SNAPSHOT");
            service.setNetworks(Arrays.asList("mleap-bridge"));
            service.setVolumes(Arrays.asList("./models:/models"));
            services.set(mLeapEntity.getModelName(), yamlObjectMapper.valueToTree(service));
        }

        // 添加bridge的service
        DockerComposeDTO.Service bridge = dockerComposeDTO.new Service();
        bridge.setImage(customConfig.getHarborIp() + "/zrar/mleap-bridge:1.0.0");
        bridge.setNetworks(Arrays.asList("mleap-bridge"));
        bridge.setPorts(Arrays.asList("8083:8080"));
        services.set("bridge", yamlObjectMapper.valueToTree(bridge));

        dockerComposeDTO.setServices(services);

        // 配置网络
        DockerComposeDTO.Networks networks = dockerComposeDTO.new Networks();
        DockerComposeDTO.Networks.MleapBridge mleapBridge = networks.new MleapBridge();
        mleapBridge.setDriver("bridge");
        DockerComposeDTO.Networks.MleapBridge.Ipam ipam = mleapBridge.new Ipam();
        ipam.setDriver("default");
        DockerComposeDTO.Networks.MleapBridge.Ipam.Config config = ipam.new Config();
        config.setSubnet("10.1.1.48/28");
        ipam.setConfig(Arrays.asList(config));
        mleapBridge.setIpam(ipam);
        networks.setMleapBridge(mleapBridge);
        dockerComposeDTO.setNetworks(networks);

        // 将结果写入docker-compose.yml文件中
        yamlObjectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(customConfig.getDockerComposePath()), dockerComposeDTO);
    }
}
