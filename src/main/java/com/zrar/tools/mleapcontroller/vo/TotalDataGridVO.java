package com.zrar.tools.mleapcontroller.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Data
public class TotalDataGridVO {
    private Integer total;
    private List<ModelVO> rows;
}
