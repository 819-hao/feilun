package com.seeease.flywheel.recycle.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 详情接口
 *
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RecycleOrderDetailsRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 单号
     */
    private String serialNo;
}
