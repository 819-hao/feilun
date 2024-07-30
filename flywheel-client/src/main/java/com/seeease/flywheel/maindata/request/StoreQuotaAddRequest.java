package com.seeease.flywheel.maindata.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreQuotaAddRequest implements Serializable {

    private Integer id;
    /**
     * 门店id
     */
    private Integer shopId;
    /**
     * 开始时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 是否控制
     */
    private Integer isCtl;
    /**
     * 压货列表
     */
    private List<Line> osQuotas;
    /**
     * 代销列表
     */
    private List<Line> ctQuotas;

    @Data
    public static class Line implements Serializable{
        /**
         * 品牌id
         */
        private Integer brandId;
        /**
         * 控制的额度
         */
        private BigDecimal quota;


    }

}
