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
    MODEL_ONLINE_FAILED(7, "模型上线失败"),
    JSON_ERROR(8, "JSON错误"),
    PREDICT_ERROR(9, "预测错误"),
    UNKNOWN_CUT_METHOD_ERROR(10, "未知切词方式"),
    UNKNOWN_NAME_ERROR(11, "未知的Name字段"),
    GET_MODEL_NAMES_ERROR(12, "从docker-compose.yml中获取模型的名称列表失败"),
    CAN_NOT_FIND_MODEL_ERROR(13, "找不到模型文件"),
    UNKNOWN_ERROR(14, "未知错误"),
    ;

    Integer code;
    String message;
}
