package com.zrar.tools.mleapcontroller.controller;

import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.DatabaseService;
import com.zrar.tools.mleapcontroller.service.FileService;
import com.zrar.tools.mleapcontroller.service.WebService;
import com.zrar.tools.mleapcontroller.util.ResultUtils;
import com.zrar.tools.mleapcontroller.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private MLeapRepository mLeapRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private FileService fileService;

    @Autowired
    private Runtime runtime;

    @Value("${spring.profiles.active}")
    private String active;

    @GetMapping("/getAll")
    public TotalMLeapFileDataGridVO getAll() {
        List<MLeapFileVO> mLeapFileVOList = webService.getAllMLeapFileVOList();

        TotalMLeapFileDataGridVO totalMLeapFileDataGridVO = new TotalMLeapFileDataGridVO();
        totalMLeapFileDataGridVO.setTotal(mLeapFileVOList.size());
        totalMLeapFileDataGridVO.setRows(mLeapFileVOList);
        return totalMLeapFileDataGridVO;
    }

    /**
     * 获取所有切词方式
     * @return
     */
    @GetMapping("/getCutMethodVOList")
    public List<CutMethodVO> getCutMethodVOList() {
        return webService.getCutMethodVOList();
    }

    /**
     * 获取所有模型列表
     * @return
     */
    @GetMapping("/getModelVOList")
    public List<ModelVO> getModelVOList() {
        return webService.getModelVOList();
    }

    /**
     * 添加模型
     * @param modelName
     * @param modelDesc
     * @param cutMethodName
     * @return
     */
    @PostMapping("/addModel")
    public ResultVO addModel(String modelName,
                             String modelDesc,
                             String cutMethodName) {
        MLeapEntity mLeapEntity = new MLeapEntity();
        mLeapEntity.setModelName(modelName);
        mLeapEntity.setModelDesc(modelDesc);
        mLeapEntity.setCutMethodName(cutMethodName);
        mLeapRepository.save(mLeapEntity);
        return ResultUtils.success();
    }

    /**
     * 删除模型
     * @param modelName
     * @return
     */
    @PostMapping("/delModel")
    public ResultVO delModel(String modelName) {
        mLeapRepository.deleteByModelName(modelName);

        // 删除模型文件
        String modelPath = fileService.getModelOutterPath(modelName);
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            modelFile.delete();
        }

        if (active.equalsIgnoreCase("dev")) {
            try {
                // 删除远程服务器上面的模型文件
                String cmd = "ssh " + customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp()
                        + " \"rm -f /opt/docker/mleap/" + modelName + ".zip\"";
                Process process = runtime.exec(cmd);
                log.debug("{} return {}", cmd, process.waitFor());
            } catch (Exception e) {
                log.error("e = {}", e);
                System.exit(-1);
            }
        }

        return ResultUtils.success();
    }

    /**
     * 添加完模型，仅仅是在数据库中添加了一条记录，还需要通过重启使服务真实生效
     * @return
     */
    @RequestMapping("/restart")
    public ResultVO restart() {
        try {
            databaseService.init();
        } catch (Exception e) {
            log.error("e = {}", e);
            System.exit(-1);
        }
        return ResultUtils.success();
    }
}
