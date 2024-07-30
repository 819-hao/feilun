package com.seeease.flywheel.web.entity.request;

import com.seeease.flywheel.web.controller.xianyu.enums.XianYuCloseReasonCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 取消订单
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderCancelRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 原因
     */
    private XianYuCloseReasonCodeEnum reason;
}
