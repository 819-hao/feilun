package com.seeease.flywheel.maindata.result;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferUsableQuotaQueryResult implements Serializable {


    private Integer isCtl = 0;
    /**
     * 已使用压货额度
     */
    private BigDecimal usedCtQuota = BigDecimal.ZERO;
    /**
     * 可用代销总额度
     */
    private BigDecimal osQuota = BigDecimal.ZERO;

    private List<Item> list = Collections.emptyList();


    @Data
    public static class Item implements Serializable{
        private Integer brandId;
        private String brandName;
        private BigDecimal osQuota;
    }




}
