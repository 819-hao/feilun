package com.seeease.flywheel.recycle.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 回收取消订单
 *
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class RecycleOrderAdminCancelResult implements Serializable {

    private Integer id;

    private String serialNo;

    private String msg;
}
