package com.zrar.tools.mleapcontroller.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import com.zrar.tools.mleapcontroller.util.ResultUtils;
import com.zrar.tools.mleapcontroller.vo.ResultVO;
import com.zrar.tools.mleapcontroller.vo.TaxClassifyPredictVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 接收一个模型文件，让它上线
     * 实际上调用mleap的PUT /model方法
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("/{mleap}/onlineModel")
    public ResultVO onlineModel(@PathVariable("mleap") String mleap,
                                @RequestParam("file") MultipartFile multipartFile) {

        log.debug("mleap = {}, file = {}", mleap, multipartFile);

        // 判断上传过来的文件是不是空的
        if (multipartFile.isEmpty()) {
            return ResultUtils.error(ResultEnum.FILE_CAN_NOT_BE_EMPTY);
        }

        // 获取文件名
        String filename = multipartFile.getOriginalFilename();
        File folder = new File("/models");
        File file = new File(folder, filename);

        // 将上传上来的文件保存到/models目录
        try {
            byte[] data = multipartFile.getBytes();
            // 如果待保存的文件夹不存在，那就创建一个文件夹
            if (!folder.exists()) {
                folder.mkdirs();
            }
            // 如果目标文件存在，删除它
            if (file.exists()) {
                file.delete();
            }
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

        // 让模型上线
        try {
            String result = mLeapService.online(mleap, file);

            // 将更新或添加模型数据到数据库中
            MLeapEntity mLeapEntity = mLeapRepository.findByMleapName(mleap);
            if (mLeapEntity == null) {
                mLeapEntity = new MLeapEntity();
                mLeapEntity.setMleapName(mleap);
            }
            mLeapEntity.setModelPath(file.getAbsolutePath());
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
    @PostMapping("/{mleap}/offlineModel")
    public ResultVO offlineModel(@PathVariable("mleap") String mleap) {
        String result = mLeapService.offline(mleap);

        MLeapEntity mLeapEntity = mLeapRepository.findByMleapName(mleap);
        if (mLeapEntity != null) {
            // 删除数据库记录，同时删除模型文件
            mLeapRepository.deleteById(mLeapEntity.getId());
            File file = new File(mLeapEntity.getModelPath());
            if (file.exists()) {
                file.delete();
            }
        }

        return ResultUtils.success(result);
    }

    /**
     * 调用模型，返回预测结果
     * 实际上调用mleap的POST /transform方法
     *
     * @param data
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
