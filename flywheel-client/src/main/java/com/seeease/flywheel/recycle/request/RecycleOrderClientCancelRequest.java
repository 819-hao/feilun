package com.seeease.flywheel.recycle.request;

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
public class RecycleOrderClientCancelRequest implements Serializable {

    /**
     * id
     */
    private Integer id;
}
