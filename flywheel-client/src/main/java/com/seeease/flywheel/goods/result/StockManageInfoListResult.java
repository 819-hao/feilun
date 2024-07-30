package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/8/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockManageInfoListResult implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 库存商品id
     */
    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 盒子编号
     */
    private String boxNumber;

    /**
     * 库位
     */
    private Integer storageId;

    /**
     * 库位大区
     */
    private String storageRegion;

    /**
     * 库位子区
     */
    private String storageSubsegment;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;
}
