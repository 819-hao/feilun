package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
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
public class StockManageInfoListRequest extends PageRequest implements Serializable {

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 盒子编号
     */
    private String boxNumber;

    /**
     * 库位大区
     */
    private String storageRegion;

    /**
     * 库位子区
     */
    private String storageSubsegment;
}
