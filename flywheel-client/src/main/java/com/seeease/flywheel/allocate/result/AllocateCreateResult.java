package com.seeease.flywheel.allocate.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateCreateResult implements Serializable {
    /**
     * 调拨单
     */
    List<AllocateDto> allocateDtoList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocateDto implements Serializable {
        /**
         * 调拨单号
         */
        private String serialNo;

        /**
         * 调拨来源
         */
        private Integer allocateSource;

        /**
         * 调拨类型
         */
        private Integer allocateType;

        /**
         * 发货方门店简码
         */
        private String fromShopShortcodes;

        /**
         * 收货方门店简码
         */
        private String toShopShortcodes;

        /**
         * 发货单
         */
        private List<AllocateWorkDto> workSerialNoList;

        /**
         * 调拨行商品
         */
        private List<Integer> stockIdList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocateWorkDto implements Serializable {
        /**
         * 发货单号
         */
        private String ckSerialNo;
        /**
         * 收货单号
         */
        private String rkSerialNo;

    }
}
