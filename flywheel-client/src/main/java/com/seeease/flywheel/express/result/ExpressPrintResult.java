package com.seeease.flywheel.express.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description demo
 * @Date create in 2023/6/25 13:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressPrintResult implements Serializable {

    /**
     * 快递单号 list
     */
    private List<String> expressNoList;

    private String requestID;

    private String accessToken;

    private String templateCode;

    private String productName;

    private String remarks;

}
