package com.zrar.tools.mleapcontroller.util;

import com.zrar.tools.mleapcontroller.constant.ResultEnum;
import com.zrar.tools.mleapcontroller.vo.ResultVO;

/**
 * Created by 廖师兄
 * 2017-01-21 13:39
 */
public class ResultUtils {

    public static ResultVO success(Object object) {
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("成功");
        result.setData(object);
        return result;
    }

    public static ResultVO success() {
        return success(null);
    }

    public static ResultVO error(Integer code, String msg) {
        ResultVO result = new ResultVO();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static ResultVO error(ResultEnum resultEnum) {
        return error(resultEnum.getCode(), resultEnum.getMessage());
    }
}
