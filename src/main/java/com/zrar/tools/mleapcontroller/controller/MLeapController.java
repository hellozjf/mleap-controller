package com.zrar.tools.mleapcontroller.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import com.zrar.tools.mleapcontroller.service.FileService;
import com.zrar.tools.mleapcontroller.util.ResultUtils;
import com.zrar.tools.mleapcontroller.vo.ResultVO;
import com.zrar.tools.mleapcontroller.vo.TaxClassifyPredictVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Jingfeng Zhou
 */
@RestController
@Slf4j
public class MLeapController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MLeapService mLeapService;

    @Autowired
    private MLeapRepository mLeapRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private CustomConfig customConfig;

    @Value("${spring.profiles.active}")
    private String active;

    @Autowired
    private Runtime runtime;

    /**
     * 接收一个模型文件，让它上线
     * 实际上调用mleap的PUT /model方法
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("/{modelName}/onlineModel")
    public ResultVO onlineModel(@PathVariable("modelName") String modelName,
                                @RequestParam("file") MultipartFile multipartFile) {

        log.debug("modelName = {}, file = {}", modelName, multipartFile);

        // 判断上传过来的文件是不是空的
        if (multipartFile.isEmpty()) {
            return ResultUtils.error(ResultEnum.FILE_CAN_NOT_BE_EMPTY);
        }

        // 获取文件名
        File file = new File(fileService.getModelOutterPath(modelName));

        // 将上传上来的文件保存到 mleapConfig.modelOuterPath 目录下
        try {
            byte[] data = multipartFile.getBytes();
            // 然后把文件保存下来
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                 FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                IOUtils.copy(byteArrayInputStream, fileOutputStream);
            } catch (IOException e) {
                log.error("e = {}", e);
                if (file.exists()) {
                    file.delete();
                }
                return ResultUtils.error(ResultEnum.FILE_IS_WRONG.getCode(), e.getMessage());
            }
        } catch (IOException e) {
            log.error("e = {}", e);
            if (file.exists()) {
                file.delete();
            }
            return ResultUtils.error(ResultEnum.FILE_IS_WRONG.getCode(), e.getMessage());
        }

        // dev版本，还需要把model文件拷贝到服务器上面去
        if (active.equalsIgnoreCase("dev")) {
            try {
                String cmd = "scp " + fileService.getModelOutterPath(modelName) + " "
                        + customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp() + ":/opt/docker/mleap/models";
                Process process = runtime.exec(cmd);
                log.debug("{} return {}", cmd, process.waitFor());
            } catch (Exception e) {
                log.error("e = {}", e);
                System.exit(-1);
            }
        }

        // 让模型上线
        try {
            String result = mLeapService.online(modelName);

            // 将更新或添加模型数据到数据库中
            MLeapEntity mLeapEntity = mLeapRepository.findByModelName(modelName);
            if (mLeapEntity == null) {
                mLeapEntity = new MLeapEntity();
                mLeapEntity.setModelName(modelName);
            }
            mLeapRepository.save(mLeapEntity);

            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("e = {}", e);
            if (file.exists()) {
                file.delete();
            }
            return ResultUtils.error(ResultEnum.MODEL_ONLINE_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 让模型下线
     * 实际上调用mleap的DELETE /model方法
     *
     * @return
     */
    @PostMapping("/{modelName}/offlineModel")
    public ResultVO offlineModel(@PathVariable("modelName") String modelName) {
        String result = mLeapService.offline(modelName);

        MLeapEntity mLeapEntity = mLeapRepository.findByModelName(modelName);
        if (mLeapEntity != null) {
            // 删除模型文件
            File file = new File(fileService.getModelOutterPath(modelName));
            if (file.exists()) {
                file.delete();
            }

            // dev版本，还需要删除远端的模型文件
            if (active.equalsIgnoreCase("dev")) {
                try {
                    String cmd = "ssh " + customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp()
                            + " \"cd /opt/docker/mleap/models; rm -f " + file.getName() + "\"";
                    Process process = runtime.exec(cmd);
                    log.debug("{} return {}", cmd, process.waitFor());
                } catch (Exception e) {
                    log.error("e = {}", e);
                    System.exit(-1);
                }
            }
        }

        return ResultUtils.success(result);
    }

    /**
     * 调用模型，返回预测结果
     * 实际上调用mleap的POST /transform方法
     *
     * @param data {"schema":{"fields":[{"name":"word","type":"string"}]},"rows":[["增值税 的 税率 是 多少"]]}
     * @return
     */
    @PostMapping("/{mleap}/invokeModel")
    public ResultVO invokeModel(@PathVariable("mleap") String mleap,
                                @RequestBody String data) {
        try {
            String result = mLeapService.transform(mleap, data);
            JsonNode objectNode = objectMapper.readTree(result);
            return ResultUtils.success(objectNode);
        } catch (IOException e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.INVOKE_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 预测某句话的分类，以及这个分类的可信度
     * predict实际上会发送
     * {"schema":{"fields":[{"name":"word","type":"string"}]},"rows":[["增值税 的 税率 是 多少"]]}
     *
     * @param mleap
     * @param line
     * @return
     */
    @PostMapping("/{mleap}/predict")
    public ResultVO predict(@PathVariable("mleap") String mleap,
                            @RequestBody String line,
                            @RequestParam(defaultValue = "") String nature) {
        TaxClassifyPredictVO taxClassifyPredictVO = mLeapService.predict(mleap, line, nature);
        return ResultUtils.success(taxClassifyPredictVO);
    }

    /**
     * 预测多句话的分类，以及这个分类的可信度
     *
     * @param mleap
     * @param lines
     * @return
     */
    @PostMapping("/{mleap}/predict2")
    public ResultVO predict2(@PathVariable("mleap") String mleap,
                             @RequestBody String lines,
                             @RequestParam(defaultValue = "") String nature) {
        log.debug("lines = {}", lines);
        String[] lineArray = lines.split("(\r\n)|(\n)");
        for (String line : lineArray) {
            log.debug("line = {}", line);
        }
        List<String> lineList = Arrays.asList(lineArray);
        List<TaxClassifyPredictVO> taxClassifyPredictVOList = mLeapService.predict(mleap, lineList, nature);
        return ResultUtils.success(taxClassifyPredictVOList);
    }

}
