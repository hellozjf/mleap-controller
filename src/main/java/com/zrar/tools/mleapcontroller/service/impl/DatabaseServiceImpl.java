package com.zrar.tools.mleapcontroller.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.dto.DockerComposeDTO;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.DatabaseService;
import com.zrar.tools.mleapcontroller.service.FileService;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private MLeapService mLeapService;

    @Autowired
    private Runtime runtime;

    @Autowired
    private FileService fileService;

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    public void init() throws Exception {

        // 创建相关文件夹
        File folder = new File(customConfig.getModelOuterPath());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (active.equalsIgnoreCase("dev")) {
            String cmd = "ssh " +
                    customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp()
                    + " \"mkdir -p /opt/docker/mleap\"";
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        }

        // 删除原有的docker-compose.yml创建的容器
        if (active.equalsIgnoreCase("prod")) {
            String cmd = "docker-compose down";
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        } else if (active.equalsIgnoreCase("dev")){
            String cmd = "ssh " +
                    customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp()
                    + " \"cd /opt/docker/mleap; docker-compose down\"";
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        }

        // 从数据库中生成新的docker-compose.yml文件
        generateDockerComposeYml();
        if (active.equalsIgnoreCase("dev")) {
            String cmd = "scp " + customConfig.getDockerComposePath()
                    + " " + customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp() + ":/opt/docker/mleap";
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        }

        // 用新的docker-compose.yml文件创建容器
        if (active.equalsIgnoreCase("prod")) {
            String cmd = "docker-compose up -d";
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        } else if (active.equalsIgnoreCase("dev")) {
            String cmd = "ssh " +
                    customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp()
                    + " \"cd /opt/docker/mleap; docker-compose up -d\"";
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        }

        // 将模型都加载到容器中
        if (active.equalsIgnoreCase("prod")) {
            uploadModels();
        } else if (active.equalsIgnoreCase("dev")) {
            // 把本地的模型全部拷贝到远端
            String cmd = "scp -r " + customConfig.getModelOuterPath()
                    + " " + customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp() + ":/opt/docker/mleap";
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
            uploadModels();
        }
    }

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

    @Override
    public void uploadModels() {
        List<MLeapEntity> mLeapEntityList = mLeapRepository.findAll();
        while (true) {
            try {
                for (MLeapEntity mLeapEntity : mLeapEntityList) {
                    String modelName = mLeapEntity.getModelName();
                    String modelPath = fileService.getModelOutterPath(modelName);
                    File modelFile = new File(modelPath);
                    if (modelFile.exists()) {
                        log.debug("开始恢复{}", modelName);
                        mLeapService.online(modelName);
                    }
                }
                log.debug("所有模型恢复成功");
                break;
            } catch (Exception e) {
                log.error("e = {}", e.getMessage());
                try {
                    log.debug("捕获到异常，说明docker服务还没有启动好，等待10秒后再次upload");
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e1) {
                    log.error("e1 = {}", e1);
                }
            }
        }
    }
}
