package com.seeease.flywheel.web.controller.xianyu.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/10/13
 */
@Data
public class QiMenBaseResult implements Serializable {
    private String errCode = "0";
    private String errMessage = "OK";
    private boolean success = true;

    /**
     * 签名失败
     *
     * @return
     */
    public static QiMenBaseResult buildSignFail() {
        QiMenBaseResult result = new QiMenBaseResult();
        result.setErrCode("-1");
        result.setErrMessage("Invalid Signature");
        result.setSuccess(false);
        return result;
    }

    /**
     * 处理失败
     *
     * @return
     */
    public static QiMenBaseResult buildFail() {
        QiMenBaseResult result = new QiMenBaseResult();
        result.setErrCode("-1");
        result.setErrMessage("处理失败");
        result.setSuccess(false);
        return result;
    }

}
