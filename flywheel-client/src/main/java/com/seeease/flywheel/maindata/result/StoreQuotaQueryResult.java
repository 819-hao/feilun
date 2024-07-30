package com.seeease.flywheel.maindata.result;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.seeease.flywheel.maindata.request.StoreQuotaAddRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreQuotaQueryResult implements Serializable {


    /**
     * smid
     */
    private Integer id;
    /**
     * 门店id
     */
    private Integer shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    /**
     * 是否控制
     */
    private Integer isCtl;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;
    /**
     * 压货
     */
    private List<StoreQuotaQueryResult.Line> osQuotas;
    /**
     * 代销
     */
    private List<StoreQuotaQueryResult.Line> ctQuotas;
    /**
     * 压货总额度
     */
    private BigDecimal osQuota;
    /**
     * 已使用压货总额度
     */
    private BigDecimal usedOsQuota;
    /**
     * 代销总额度
     */
    private BigDecimal ctQuota;
    /**
     * 已使用代销总额度
     */
    private BigDecimal usedCtQuota;
    @Data
    public static class Line implements Serializable{

        private Integer brandId;

        private BigDecimal quota;

        private String brandName;

    }






}
