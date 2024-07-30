package com.seeease.flywheel.recycle.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商城回购创建采购单
 *
 * @Auther Gilbert
 * @Date 2023/9/4 16:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class RecycleRefuseReturnResult implements Serializable {

    /**
     * 主键id
     */
    private Integer id;
}
