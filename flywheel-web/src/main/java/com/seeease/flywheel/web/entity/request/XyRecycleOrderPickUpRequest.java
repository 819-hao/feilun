package com.seeease.flywheel.web.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 取件
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderPickUpRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 物流单号
     */
    private String expressNumber;

}
