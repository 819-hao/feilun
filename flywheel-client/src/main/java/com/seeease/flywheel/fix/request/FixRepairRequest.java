package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description 新接修入参
 * @Date create in 2023/11/13 14:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixRepairRequest implements Serializable {

    /**
     * 维修id
     */
    private Integer fixId;

    /**
     * 维修单号
     */
    private String serialNo;

    /**
     * 维修天数
     */
    private Integer fixDay;

    /**
     * 维修项
     */
    private List<FixRepairRequest.FixProjectMapper> content;

    /**
     * 附件id list
     */
    private List<Integer> attachmentIdList;

    /**
     * 瑕疵说明
     */
    private String defectDescription;

    /**
     * 维修配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixProjectMapper implements Serializable {

        private Integer fixProjectId;

        private BigDecimal fixMoney;
    }

    /**
     * 1 接受 0 拒绝
     */
    private Integer accept;

    /**
     * 入参数据
     */
    private Map<Integer, BigDecimal> stockMap;

    /**
     * 返回备注
     */
    private String returnRemark;

    /**
     * 送修备注
     */
    private String fixRemark;

}
