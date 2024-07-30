package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 维修结果
 * @Date create in 2023/2/3 11:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixEditRequest implements Serializable {

    private Integer fixId;

    private Integer fixType;

    private Integer fixDay;

    private String serialNo;

    private Integer maintenanceMasterId;

    private List<FixProjectMapper> content;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixProjectMapper implements Serializable {

        private Integer fixProjectId;

        private BigDecimal fixMoney;
    }

    /**
     * 附件id list
     */
    private List<Integer> attachmentIdList;

    private Integer defectOrNot;

    private String defectDescription;

    private String remark;

    private Integer specialExpediting;

    /**
     * 返回备注
     */
    private String returnRemark;

    /**
     * 送修备注
     */
    private String fixRemark;
}
