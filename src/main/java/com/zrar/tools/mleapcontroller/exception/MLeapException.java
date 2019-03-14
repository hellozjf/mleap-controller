package com.zrar.tools.mleapcontroller.exception;

import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import lombok.Getter;

/**
 * @author Jingfeng Zhou
 */
@Getter
public class MLeapException extends RuntimeException {

    private Integer code;

    public MLeapException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public MLeapException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
