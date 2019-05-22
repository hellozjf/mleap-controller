package com.zrar.tools.mleapcontroller.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.service.DockerService;
import com.zrar.tools.mleapcontroller.service.FileService;
import com.zrar.tools.mleapcontroller.service.WebService;
import com.zrar.tools.mleapcontroller.util.ResultUtils;
import com.zrar.tools.mleapcontroller.vo.CutMethodVO;
import com.zrar.tools.mleapcontroller.vo.ModelVO;
import com.zrar.tools.mleapcontroller.vo.ResultVO;
import com.zrar.tools.mleapcontroller.vo.TotalDataGridVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Jingfeng Zhou
 */
@Slf4j
@RestController
@RequestMapping("/web")
public class WebController {

    @Autowired
    private WebService webService;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileService fileService;

    @GetMapping("/getAll")
    public TotalDataGridVO getAll() {
        List<ModelVO> modelVOList = webService.getModelVOList();

        TotalDataGridVO totalDataGridVO = new TotalDataGridVO();
        totalDataGridVO.setTotal(modelVOList.size());
        totalDataGridVO.setRows(modelVOList);
        return totalDataGridVO;
    }

    /**
     * 获取所有切词方式
     *
     * @return
     */
    @GetMapping("/getCutMethodVOList")
    public List<CutMethodVO> getCutMethodVOList() {
        return webService.getCutMethodVOList();
    }

    /**
     * 获取所有模型列表
     *
     * @return
     */
    @GetMapping("/getModelVOList")
    public List<ModelVO> getModelVOList() {
        return webService.getModelVOList();
    }

    /**
     * 添加模型
     *
     * @param modelName
     * @param modelDesc
     * @param cutMethodName
     * @return
     */
    @PostMapping("/addModel")
    public ResultVO addModel(String modelName,
                             String modelDesc,
                             String cutMethodName) {
        try {
            MLeapEntity mLeapEntity = webService.addModel(modelName, modelDesc, cutMethodName);
            return ResultUtils.success(mLeapEntity);
        } catch (Exception e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.ADD_MODEL_ERROR);
        }
    }

    /**
     * 删除模型
     *
     * @param modelName
     * @return
     */
    @PostMapping("/delModel")
    public ResultVO delModel(String modelName) {
        try {
            webService.delModel(modelName);
            return ResultUtils.success(modelName);
        } catch (Exception e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.DEL_MODEL_ERROR);
        }
    }

    /**
     * 更新模型列表
     *
     * @return
     */
    @PostMapping("/updateModel")
    public ResultVO updateModel(String modelName,
                                String modelDesc,
                                String cutMethodName) {
        MLeapEntity mLeapEntity = webService.updateModel(modelName, modelDesc, cutMethodName);
        return ResultUtils.success(mLeapEntity);
    }

    @RequestMapping("/downloadModel")
    public ResponseEntity<byte[]> downloadModel(String modelName) {

        // 将模型文件读取到byte数组中
        String modelFilePath = fileService.getModelOutterPath(modelName);
        File file = new File(modelFilePath);
        byte[] bytes = null;
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            IOUtils.copy(fileInputStream, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("e = {}", e.getMessage());
        }

        // 告诉浏览器，以附件的形式下载
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDispositionFormData("attachment", modelName + ".zip");
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * 把批量删除、更改、新增模型的工作交给后台做，这样前端就能显示保存中进度条了
     *
     * @param deletedRows
     * @param updatedRows
     * @param insertedRows
     * @return
     */
    @PostMapping("/addDelUpdateModels")
    public ResultVO addDelUpdateModels(String deletedRows,
                                       String updatedRows,
                                       String insertedRows) {
        log.debug("deletedRows = {}", deletedRows);
        log.debug("updatedRows = {}", updatedRows);
        log.debug("insertedRows = {}", insertedRows);
        try {
            ArrayNode deletedNodes = (ArrayNode) objectMapper.readTree(deletedRows);
            ArrayNode updatedNodes = (ArrayNode) objectMapper.readTree(updatedRows);
            ArrayNode insertedNodes = (ArrayNode) objectMapper.readTree(insertedRows);
            for (JsonNode deletedNode : deletedNodes) {
                String modelName = deletedNode.get("modelName").asText();
                webService.delModel(modelName);
            }
            for (JsonNode updatedNode : updatedNodes) {
                String modelName = updatedNode.get("modelName").asText();
                String modelDesc = updatedNode.get("modelDesc").asText();
                String modelCutMethodName = updatedNode.get("modelCutMethodName").asText();
                webService.updateModel(modelName, modelDesc, modelCutMethodName);
            }
            for (JsonNode insertedNode : insertedNodes) {
                String modelName = insertedNode.get("modelName").asText();
                String modelDesc = insertedNode.get("modelDesc").asText();
                String modelCutMethodName = insertedNode.get("modelCutMethodName").asText();
                webService.addModel(modelName, modelDesc, modelCutMethodName);
            }
            return ResultUtils.success();
        } catch (Exception e) {
            log.error("e = {}", e);
            return ResultUtils.error(ResultEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 添加完模型，仅仅是在数据库中添加了一条记录，还需要通过重启使服务真实生效
     *
     * @return
     */
    @RequestMapping("/restart")
    public ResultVO restart() {
        dockerService.init();
        return ResultUtils.success();
    }

    /**
     * 判断docker服务是否已经正常启动
     * @return
     */
    @GetMapping("/isStarted")
    public ResultVO isStarted() {
        return ResultUtils.success(dockerService.isStarted());
    }

    /**
     * 这个controller只有当docker相关服务全部正常启动之后才返回，避免用户刷新了页面又能进行相关操作
     * @return
     */
    @GetMapping("/waitForStarted")
    public ResultVO waitForStarted() {
        while (! dockerService.isStarted()) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                log.error("e = {}", e);
            }
        }
        return ResultUtils.success();
    }
}
