package com.seeease.flywheel.helper.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BreakPriceAuditPageRequest extends PageRequest {
    /**
     * id
     */
    private Integer id;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 表身号
     */
    private String sn;
    /**
     * 门店id
     */
    private Integer shopId;
  
}
