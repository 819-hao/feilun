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
 * @Description 维修完成
 * @Date create in 2023/1/17 17:25
 */
@Data
public class FixFinishRequest implements Serializable {

    /**
     * 维修完成 1
     * 维修取消 0
     */
    private Integer finishType;

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
    private List<FixFinishRequest.FixProjectMapper> content;

    /**
     * 附件id list
     */
    private List<Integer> attachmentIdList;

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
}
