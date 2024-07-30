package com.seeease.flywheel.k3cloud.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/7 11:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class K3cloudGlVoucherResult implements Serializable {

    /**
     * 需求内码
     */
    private List<Integer> list;
}
