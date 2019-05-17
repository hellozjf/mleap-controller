package com.zrar.tools.mleapcontroller.vo;

import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
public class MLeapFileVO {
    private String id;
    private String url;
    private String fileName;
    private String fileSize;
    private String fileDate;
    private String cutMethod;
    private String cutMethodName;
    private String desc;
}
