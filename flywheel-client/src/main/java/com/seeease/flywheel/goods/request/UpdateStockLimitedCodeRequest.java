package com.seeease.flywheel.goods.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2024/1/8
 */
@Data
public class UpdateStockLimitedCodeRequest implements Serializable {

    private Integer stockId;

    private String limitedCode;
}
