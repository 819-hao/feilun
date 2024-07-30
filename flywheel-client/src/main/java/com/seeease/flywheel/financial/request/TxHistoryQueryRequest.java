package com.seeease.flywheel.financial.request;


import com.seeease.flywheel.PageRequest;

import lombok.*;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data

@NoArgsConstructor
public class TxHistoryQueryRequest extends PageRequest {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;

    /**
     *编号
     */
    private String serial;

    /**
     *电话
     */
    private String phone;

    /**
     *销售方名称
     */
    private String sellerName;
    /**
     *身份证号码
     */
    private String idCard;
    /**
     *商品编码
     */
    private String sn;

}
