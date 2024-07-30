package com.seeease.flywheel.web.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 退货
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderRefundRequest implements Serializable {

    /**
     * id
     */
    private Integer id;
    /**
     * 退货物流单号
     */
    private String refundExpressNumber;

    /**
     * 退货面单图
     */
    private String refundFaceImages;

    /**
     * 退货实物图
     */
    private List<String> refundGoodsImages;
}
