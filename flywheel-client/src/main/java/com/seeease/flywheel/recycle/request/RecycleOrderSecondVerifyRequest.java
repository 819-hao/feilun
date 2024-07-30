package com.seeease.flywheel.recycle.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 回收、回购请求接口
 *
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RecycleOrderSecondVerifyRequest implements Serializable {

    /**
     * id
     */
    private Integer id;
    /**
     * 客户是否接受(0：接受 1:不接受)
     */
    private Integer accept;

    private String deliveryExpressNumber;
}
