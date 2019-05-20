package com.zrar.tools.mleapcontroller.controller;

import com.zrar.tools.mleapcontroller.service.WebService;
import com.zrar.tools.mleapcontroller.vo.CutMethodVO;
import com.zrar.tools.mleapcontroller.vo.MLeapFileVO;
import com.zrar.tools.mleapcontroller.vo.ModelVO;
import com.zrar.tools.mleapcontroller.vo.TotalMLeapFileDataGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@RestController
@RequestMapping("/web")
public class WebController {

    @Autowired
    private WebService webService;

    @GetMapping("/getAll")
    public TotalMLeapFileDataGridVO getAll() {
        List<MLeapFileVO> mLeapFileVOList = webService.getAllMLeapFileVOList();

        TotalMLeapFileDataGridVO totalMLeapFileDataGridVO = new TotalMLeapFileDataGridVO();
        totalMLeapFileDataGridVO.setTotal(mLeapFileVOList.size());
        totalMLeapFileDataGridVO.setRows(mLeapFileVOList);
        return totalMLeapFileDataGridVO;
    }

    @GetMapping("/getCutMethodVOList")
    public List<CutMethodVO> getCutMethodVOList() {
        return webService.getCutMethodVOList();
    }

    @GetMapping("/getModelVOList")
    public List<ModelVO> getModelVOList() {
        return webService.getModelVOList();
    }
}
