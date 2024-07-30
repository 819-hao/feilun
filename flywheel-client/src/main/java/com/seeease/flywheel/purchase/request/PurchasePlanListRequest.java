package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 * 采购创建的基类
 *
 * @author Tiro
 * @date 2023/1/7
 */
@Data
public class PurchasePlanListRequest extends PageRequest {
    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 计划开始时间
     */
    private String planStartTime;

    /**
     * 计划结束时间
     */
    private String planEndTime;
    /**
     * 业务类型：业务类型：0-默认其他,1-新表集采
     */
    private Integer businessType;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 需方id
     */
    private Integer demanderStoreId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 导出手动选择
     */
    private List<Integer> docBatchIds;
}
