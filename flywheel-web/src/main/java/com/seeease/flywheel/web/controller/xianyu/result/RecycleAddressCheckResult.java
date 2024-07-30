package com.seeease.flywheel.web.controller.xianyu.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/10/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleAddressCheckResult extends QiMenBaseResult {

    /**
     * 持当前地区的交付类型 1:顺丰邮寄，2：上门;3：到店
     */
    private List<Long> shipTypes;
}
