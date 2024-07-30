package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 送外
 * @Date create in 2023/11/13 14:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixForeignRequest implements Serializable {

    /**
     * 接修id
     */
    private Integer fixId;


    /**
     * 维修单号
     */
    private String serialNo;

    /**
     * 送外还是送内
     */
    private Integer tagType;

    /**
     * 维修站点
     */
    private Integer fixSiteId;

    /**
     * 发货快递单号
     */
    private String deliverExpressNo;


}
