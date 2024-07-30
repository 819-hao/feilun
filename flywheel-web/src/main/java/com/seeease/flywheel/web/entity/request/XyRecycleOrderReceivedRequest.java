package com.seeease.flywheel.web.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 收货
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderReceivedRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 收货面单图
     */
    private String faceImages;

    /**
     * 收货实物图
     */
    private List<String> goodsImages;
}
