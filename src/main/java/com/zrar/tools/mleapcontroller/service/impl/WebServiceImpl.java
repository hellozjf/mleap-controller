package com.zrar.tools.mleapcontroller.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrar.tools.mleapcontroller.constant.CutMethodEnum;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.exception.MLeapException;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.FileService;
import com.zrar.tools.mleapcontroller.service.WebService;
import com.zrar.tools.mleapcontroller.vo.CutMethodVO;
import com.zrar.tools.mleapcontroller.vo.MLeapFileVO;
import com.zrar.tools.mleapcontroller.vo.ModelVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class WebServiceImpl implements WebService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("yamlObjectMapper")
    private ObjectMapper yamlObjectMapper;

    @Autowired
    private MLeapRepository mLeapRepository;

    @Autowired
    private FileService fileService;

    @Override
    public List<String> getModelNameList() {
        try {
            File file = new File(fileService.getDockerComposeYmlPath());
            Map map = yamlObjectMapper.readValue(new FileInputStream(file), Map.class);
            log.debug("docker-compose.yml = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
            Map<String, Object> services = (Map<String, Object>) map.get("services");
            List<String> modelNameList = new ArrayList<>();
            for (String key : services.keySet()) {
                if (! key.equalsIgnoreCase("bridge")) {
                    modelNameList.add(key);
                }
            }
            return modelNameList;
        } catch (IOException e) {
            log.error("e = {}", e);
            throw new MLeapException(ResultEnum.GET_MODEL_NAMES_ERROR);
        }
    }

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
        List<String> modelNameList = getModelNameList();
        List<ModelVO> modelVOList = new ArrayList<>();
        for (String modelName : modelNameList) {
            MLeapEntity mLeapEntity = mLeapRepository.findByModelName(modelName);
            if (mLeapEntity != null) {
                ModelVO modelVO = new ModelVO();
                modelVO.setId(mLeapEntity.getId());
                modelVO.setModelName(modelName);
                modelVO.setModelDesc(mLeapEntity.getModelDesc());
                File file = new File(fileService.getModelOutterPath(modelName));
                try (InputStream inputStream = new FileInputStream(file)) {
                    modelVO.setModelMd5(DigestUtils.md5DigestAsHex(inputStream));
                } catch (FileNotFoundException e) {
                    log.error("找不到模型文件{}", modelName);
                    modelVO.setModelMd5("模型未上传");
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
    public List<MLeapFileVO> getAllMLeapFileVOList() {
        List<String> modelNameList = getModelNameList();
        List<MLeapFileVO> mLeapFileVOList = modelNameList.stream()
                .map(mleap -> {
                    MLeapEntity mLeapEntity = mLeapRepository.findByModelName(mleap);
                    log.debug("mleapEntity = {}", mLeapEntity);
                    if (mLeapEntity != null) {
                        MLeapFileVO mLeapFileVO = new MLeapFileVO();
                        mLeapFileVO.setId(mLeapEntity.getId());
                        mLeapFileVO.setUrl(mLeapEntity.getModelName());

                        File file = new File(fileService.getModelOutterPath(mLeapEntity.getModelName()));
                        mLeapFileVO.setFileName(file.getName());

                        // 文件大小
                        if (file.length() < 1024) {
                            mLeapFileVO.setFileSize(file.length() + "B");
                        } else if (file.length() < 1024 * 1024) {
                            mLeapFileVO.setFileSize(String.format("%.3fKB", file.length() / 1024.0));
                        } else if (file.length() < 1024 * 1024 * 1024) {
                            mLeapFileVO.setFileSize(String.format("%.3fMB", file.length() / 1024.0 / 1024.0));
                        }

                        // 文件更新时间
                        Instant instant = Instant.ofEpochMilli(mLeapEntity.getGmtModified());
                        // ZoneId.of("Asia/Shanghai");
                        ZoneId zone = ZoneId.systemDefault();
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

                        String fileDate = localDateTime.getYear() + "年" +
                                localDateTime.getMonthValue() + "月" +
                                localDateTime.getDayOfMonth() + "日 " +
                                localDateTime.getHour() + "时" +
                                localDateTime.getMinute() + "分" +
                                localDateTime.getSecond() + "秒";
                        mLeapFileVO.setFileDate(fileDate);
                        // 查找分词方式
                        for (CutMethodEnum cutMethodEnum : CutMethodEnum.values()) {
                            if (cutMethodEnum.getName().equalsIgnoreCase(mLeapEntity.getCutMethodName())) {
                                mLeapFileVO.setCutMethod(cutMethodEnum.getName());
                                mLeapFileVO.setCutMethodName(cutMethodEnum.getDesc());
                            }
                        }
                        // 模型描述
                        mLeapFileVO.setDesc(mLeapEntity.getModelDesc());
                        return mLeapFileVO;
                    } else {
                        MLeapFileVO mLeapFileVO = new MLeapFileVO();
                        mLeapFileVO.setUrl(mleap);
                        mLeapFileVO.setFileName("");
                        mLeapFileVO.setFileSize("");
                        mLeapFileVO.setFileDate("");
                        return mLeapFileVO;
                    }
                })
                .collect(Collectors.toList());

        return mLeapFileVOList;
    }
}
