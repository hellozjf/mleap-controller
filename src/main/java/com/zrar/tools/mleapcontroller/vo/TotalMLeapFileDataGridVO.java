package com.zrar.tools.mleapcontroller.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Data
public class TotalMLeapFileDataGridVO {
    private Integer total;
    private List<MLeapFileVO> rows;
}
