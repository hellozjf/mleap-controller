package com.zrar.tools.mleapcontroller.service.impl;

import com.zrar.tools.mleapcontroller.constant.CutMethodEnum;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.exception.MLeapException;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.DockerService;
import com.zrar.tools.mleapcontroller.service.FileService;
import com.zrar.tools.mleapcontroller.service.RemoteService;
import com.zrar.tools.mleapcontroller.service.WebService;
import com.zrar.tools.mleapcontroller.vo.CutMethodVO;
import com.zrar.tools.mleapcontroller.vo.ModelVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class WebServiceImpl implements WebService {

    @Autowired
    private MLeapRepository mLeapRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private RemoteService remoteService;

    @Autowired
    private Runtime runtime;

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    @Cacheable(value = "WebService", key = "'getCutMethodVOList'")
    public List<CutMethodVO> getCutMethodVOList() {
        List<CutMethodVO> cutMethodVOList = new ArrayList<>();
        for (CutMethodEnum cutMethodEnum : CutMethodEnum.values()) {
            CutMethodVO cutMethodVO = new CutMethodVO();
            cutMethodVO.setCutMethodName(cutMethodEnum.getName());
            cutMethodVO.setCutMethodDesc(cutMethodEnum.getDesc());
            cutMethodVOList.add(cutMethodVO);
        }
        return cutMethodVOList;
    }

    @Override
    public List<ModelVO> getModelVOList() {
        List<MLeapEntity> mLeapEntityList = mLeapRepository.findAll();
        List<ModelVO> modelVOList = new ArrayList<>();
        for (MLeapEntity mLeapEntity : mLeapEntityList) {
            if (mLeapEntity != null) {
                ModelVO modelVO = new ModelVO();
                modelVO.setId(mLeapEntity.getId());
                modelVO.setModelName(mLeapEntity.getModelName());
                modelVO.setModelDesc(mLeapEntity.getModelDesc());
                File file = new File(fileService.getModelOutterPath(mLeapEntity.getModelName()));
                try (InputStream inputStream = new FileInputStream(file)) {
                    modelVO.setModelMd5(DigestUtils.md5DigestAsHex(inputStream));
                } catch (FileNotFoundException e) {
                    log.error("找不到模型文件{}", mLeapEntity.getModelName());
                    modelVO.setModelMd5("");
                } catch (IOException e) {
                    log.error("e = {}", e);
                    throw new MLeapException(ResultEnum.UNKNOWN_ERROR.getCode(), e.getMessage());
                }
                String cutMethodName = mLeapEntity.getCutMethodName();
                modelVO.setModelCutMethodName(cutMethodName);
                for (CutMethodEnum cutMethodEnum : CutMethodEnum.values()) {
                    if (cutMethodEnum.getName().equalsIgnoreCase(cutMethodName)) {
                        modelVO.setModelCutMethodDesc(cutMethodEnum.getDesc());
                    }
                }
                modelVOList.add(modelVO);
            }
        }
        return modelVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MLeapEntity addModel(String modelName, String modelDesc, String cutMethodName) throws Exception {

        // 将模型内容写到数据库中
        MLeapEntity mLeapEntity = new MLeapEntity();
        mLeapEntity.setModelName(modelName);
        mLeapEntity.setModelDesc(modelDesc);
        mLeapEntity.setCutMethodName(cutMethodName);
        mLeapEntity = mLeapRepository.save(mLeapEntity);

        // 生成docker-compose.yml文件，并更新远端服务器上的docker-compose.yml文件
        dockerService.generateDockerComposeYml();
        dockerService.copyDockerComposeYml();

        // 开启一个docker容器
        dockerService.createDocker(modelName);

        return mLeapEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delModel(String modelName) throws Exception {

        // 数据库中删除模型内容
        mLeapRepository.deleteByModelName(modelName);

        // 删除相应的docker容器
        dockerService.deleteDocker(modelName);

        // 删除模型文件
        String modelPath = fileService.getModelOutterPath(modelName);
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            modelFile.delete();
        }

        if (active.equalsIgnoreCase("dev")) {
            try {
                // 删除远程服务器上面的模型文件
                String cmd = "cd /opt/docker/mleap; rm -f /" + modelName + ".zip";
                cmd = remoteService.createExecCommand(cmd);
                Process process = runtime.exec(cmd);
                log.debug("{} return {}", cmd, process.waitFor());
            } catch (Exception e) {
                log.error("e = {}", e);
                System.exit(-1);
            }
        }

        // 生成docker-compose.yml文件，并更新远端服务器上的docker-compose.yml文件
        dockerService.generateDockerComposeYml();
        dockerService.copyDockerComposeYml();
    }

    @Override
    public MLeapEntity updateModel(String modelName, String modelDesc, String cutMethodName) {
        MLeapEntity mLeapEntity = mLeapRepository.findByModelName(modelName);
        if (mLeapEntity == null) {
            log.error("can not find modelName = {}", modelName);
        } else {
            mLeapEntity.setModelDesc(modelDesc);
            mLeapEntity.setCutMethodName(cutMethodName);
            mLeapEntity = mLeapRepository.save(mLeapEntity);
        }
        return mLeapEntity;
    }
}
