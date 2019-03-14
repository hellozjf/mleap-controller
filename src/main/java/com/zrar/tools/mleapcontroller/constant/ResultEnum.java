package com.zrar.tools.mleapcontroller.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    FILE_CAN_NOT_BE_EMPTY(1, "上传的文件不能为空"),
    FILE_SAVE_ERROR(2, "保存文件失败"),
    AUTH_FAILED(3, "服务器登录失败"),
    SCP_FAILED(4, "拷贝到服务器失败"),
    FILE_IS_WRONG(5, "上传的文件有问题"),
    INVOKE_FAILED(6, "模型调用失败"),
    ;

    Integer code;
    String message;
}
