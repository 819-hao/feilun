package com.seeease.flywheel.recycle.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 给商城发送mq状态
 * @Auther Gilbert
 * @Date 2023/9/5 15:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain=true)
public class RecycleMessage implements Serializable {
    private Integer recycleId;

    private Integer status;

    //估价单主键
    private String assessId;
    /**
     * @see com.seeease.flywheel.serve.recycle.enums.RecycleOrderPurchaseTypeEnum
     * 类型用来区分来源：回收还是置换
     */
    private Integer type;
    /**
     * @see com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum
     * 用来区分大类：回收还是回购
     */
    private Integer recycleType;
    //三方关联单号
    private String bizOrderCode;
}
