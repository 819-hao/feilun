package com.seeease.flywheel.web.entity.result;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@Data
public class DouYinDecryptResult implements Serializable {
    private String code;
    private String msg;
    /**
     * 解密结果
     */
    private Map<String, String> decryptText;
}
