package com.zrar.tools.mleapcontroller.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.dto.DockerComposeDTO;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.DockerService;
import com.zrar.tools.mleapcontroller.service.FileService;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import com.zrar.tools.mleapcontroller.service.RemoteService;
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
public class DockerServiceImpl implements DockerService {

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

    @Autowired
    private RemoteService remoteService;

    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 是否已经启动
     */
    private boolean isStarted;

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void init() {

        isStarted = false;

        // 一直重启docker相关服务，直到所有docker服务都正常启动为止
        while (true) {

            try {
                // 创建相关文件夹
                File folder = new File(customConfig.getModelOuterPath());
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                if (active.equalsIgnoreCase("dev")) {
                    String cmd = remoteService.createExecCommand("mkdir -p /opt/docker/mleap/models");
                    Process process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                }

                // 删除原有的docker-compose.yml创建的容器
                if (active.equalsIgnoreCase("prod")) {
                    String cmd = "docker-compose down";
                    Process process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                } else if (active.equalsIgnoreCase("dev")) {
                    String cmd = remoteService.createExecCommand("cd /opt/docker/mleap; docker-compose down");
                    Process process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                }

                // 从数据库中生成新的docker-compose.yml文件
                generateDockerComposeYml();
                copyDockerComposeYml();

                // 用新的docker-compose.yml文件创建容器
                if (active.equalsIgnoreCase("prod")) {
                    String cmd = "docker-compose up -d";
                    Process process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                } else if (active.equalsIgnoreCase("dev")) {
                    String cmd = remoteService.createExecCommand("cd /opt/docker/mleap; docker-compose up -d");
                    Process process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                }

                // 将模型都加载到容器中
                if (active.equalsIgnoreCase("prod")) {
                    reloadModels();
                } else if (active.equalsIgnoreCase("dev")) {
                    // 先把远端的模型全部删了
                    String cmd = remoteService.createExecCommand("cd /opt/docker/mleap/models; rm -rf *");
                    Process process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                    // 再把本地的模型全部拷贝到远端
                    cmd = remoteService.createScpCommand(customConfig.getModelOuterPath() + "/*", "/opt/docker/mleap/models/");
                    process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                    reloadModels();
                }
                break;
            } catch (Exception e) {
                log.error("e = {}", e);
            }
        }

        isStarted = true;
    }

    @Override
    public void copyDockerComposeYml() throws Exception {
        if (active.equalsIgnoreCase("dev")) {
            String cmd = remoteService.createScpCommand(customConfig.getDockerComposePath(), "/opt/docker/mleap");
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        }
    }

    @Override
    public void createDocker(String modelName) throws Exception {
        String cmd = "docker-compose run -d " + modelName;
        if (active.equalsIgnoreCase("dev")) {
            cmd = remoteService.createExecCommand("cd /opt/docker/mleap; " + cmd);
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        } else {
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        }
    }

    @Override
    public void deleteDocker(String modelName) throws Exception {
        String cmd = "docker-compose rm -f " + modelName;
        if (active.equalsIgnoreCase("dev")) {
            cmd = remoteService.createExecCommand("cd /opt/docker/mleap; " + cmd);
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
        } else if (active.equalsIgnoreCase("prod")) {
            Process process = runtime.exec(cmd);
            log.debug("{} return {}", cmd, process.waitFor());
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
    public void reloadModels() {
        List<MLeapEntity> mLeapEntityList = mLeapRepository.findAll();

        for (MLeapEntity mLeapEntity : mLeapEntityList) {
            while (true) {
                try {
                    String modelName = mLeapEntity.getModelName();
                    String modelPath = fileService.getModelOutterPath(modelName);
                    File modelFile = new File(modelPath);
                    if (modelFile.exists()) {
                        log.debug("开始恢复{}", modelName);
                        mLeapService.online(modelName);
                    }
                    break;
                } catch (Exception e) {
                    log.error("e = {}", e.getMessage());
                    try {
                        log.debug("捕获到异常，说明docker服务还没有启动好，等待5秒后再次恢复模型");
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e1) {
                        log.error("e1 = {}", e1);
                    }
                }
            }
        }
        log.debug("所有模型恢复完毕");
    }
}
