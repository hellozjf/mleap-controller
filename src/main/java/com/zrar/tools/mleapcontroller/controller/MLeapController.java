package com.zrar.tools.mleapcontroller.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.service.LocalService;
import com.zrar.tools.mleapcontroller.service.MLeapService;
import com.zrar.tools.mleapcontroller.service.RemoteService;
import com.zrar.tools.mleapcontroller.util.ResultUtils;
import com.zrar.tools.mleapcontroller.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Jingfeng Zhou
 */
@RestController
@RequestMapping("/mleap")
@Slf4j
public class MLeapController {

    @Autowired
    private MLeapService mLeapService;

    @Autowired
    private LocalService localService;

    @Autowired
    private RemoteService remoteService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 接收一个模型文件，让它上线
     * @param file
     * @return
     */
    @PostMapping("/onlineModel")
    public ResultVO onlineModel(@RequestParam("file") MultipartFile file) {

        // 判断上传过来的文件是不是空的
        if (file.isEmpty()) {
            return ResultUtils.error(ResultEnum.FILE_CAN_NOT_BE_EMPTY);
        }

        // 将上传上来的文件先保存到本地
        try {
            String filename = file.getOriginalFilename();
            byte[] data = file.getBytes();
            if (! localService.saveToLocal(filename, data)) {
                return ResultUtils.error(ResultEnum.FILE_SAVE_ERROR);
            }
        } catch (IOException e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.FILE_IS_WRONG);
        }

        // 将本地的文件通过scp拷贝到服务器上面去
        String absoluteFilePath = localService.getLocalFilePath(file.getOriginalFilename());
        if (! remoteService.scp(absoluteFilePath)) {
            return ResultUtils.error(ResultEnum.SCP_FAILED);
        }

        // 让模型上线
        String result = mLeapService.onlineModel(file.getOriginalFilename());
        return ResultUtils.success(result);
    }

    /**
     * 让模型下线
     * @return
     */
    @PostMapping("/offlineModel")
    public ResultVO offlineModel() {
        String result = mLeapService.offlineModel();
        return ResultUtils.success(result);
    }

    /**
     * 调用模型，返回预测结果
     * @param data
     * @return
     */
    @PostMapping("/invokeModel")
    public ResultVO invokeModel(@RequestBody String data) {
        try {
            String result = mLeapService.invokeModel(data);
            JsonNode objectNode = objectMapper.readTree(result);
            return ResultUtils.success(objectNode);
        } catch (IOException e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.INVOKE_FAILED);
        }
    }
}
