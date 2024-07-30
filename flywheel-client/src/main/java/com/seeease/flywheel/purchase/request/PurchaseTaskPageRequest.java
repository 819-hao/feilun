package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 采购任务列表
 * @Date create in 2023/10/25 15:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTaskPageRequest extends PageRequest implements Serializable {

    /**
     * 查询关键字
     */
    private String keyword;

    private String serialNo;

    private String startTime;

    private String endTime;

    private Integer storeId;

    private Integer taskState;


}
