package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/1
 */
@Data
public class WmsWorkCollectCountRequest implements Serializable {

    /**
     * 保持和列表同结构传参数
     */
    private WmsWorkListRequest request;

}
