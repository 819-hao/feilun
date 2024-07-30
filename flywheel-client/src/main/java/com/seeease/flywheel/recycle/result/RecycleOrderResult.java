package com.seeease.flywheel.recycle.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 回收订单返回
 * @Auther Gilbert
 * @Date 2023/9/1 09:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain=true)
public class RecycleOrderResult implements Serializable {

    private String serialNo;

    private String userId;

    private Integer recycleId;

    private Integer status;

    private String shortcodes;
}
