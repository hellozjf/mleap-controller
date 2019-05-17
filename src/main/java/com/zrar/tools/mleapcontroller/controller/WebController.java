package com.zrar.tools.mleapcontroller.controller;

import com.zrar.tools.mleapcontroller.constant.CutMethodEnum;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.WebService;
import com.zrar.tools.mleapcontroller.vo.CutMethodVO;
import com.zrar.tools.mleapcontroller.vo.MLeapFileVO;
import com.zrar.tools.mleapcontroller.vo.TotalMLeapFileDataGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@RestController
@RequestMapping("/web")
public class WebController {

    @Autowired
    private WebService webService;

    @Autowired
    private MLeapRepository mLeapRepository;

    @GetMapping("/getAll")
    public TotalMLeapFileDataGridVO getAll() {
        List<MLeapFileVO> mLeapFileVOList = webService.getAllMLeapFileVOList();

        TotalMLeapFileDataGridVO totalMLeapFileDataGridVO = new TotalMLeapFileDataGridVO();
        totalMLeapFileDataGridVO.setTotal(mLeapFileVOList.size());
        totalMLeapFileDataGridVO.setRows(mLeapFileVOList);
        return totalMLeapFileDataGridVO;
    }

    @GetMapping("/getCutMethods")
    public List<CutMethodVO> getCutMethods(String id) {

        List<CutMethodVO> cutMethodVOList = new ArrayList<>();
        for (CutMethodEnum cutMethodEnum : CutMethodEnum.values()) {
            CutMethodVO cutMethodVO = new CutMethodVO();
            cutMethodVO.setCutMethodId(cutMethodEnum.getName());
            cutMethodVO.setCutMethodText(cutMethodEnum.getDesc());
            cutMethodVOList.add(cutMethodVO);
        }
        return cutMethodVOList;
    }
}
