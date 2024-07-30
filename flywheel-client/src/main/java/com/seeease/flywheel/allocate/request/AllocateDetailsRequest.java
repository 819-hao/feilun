package com.seeease.flywheel.allocate.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateDetailsRequest implements Serializable {
    /**
     * 调拨单id
     */
    private Integer id;
    /**
     * 单号
     */
    private String serialNo;
}
