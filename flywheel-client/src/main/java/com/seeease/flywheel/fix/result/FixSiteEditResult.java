package com.seeease.flywheel.fix.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修站点编辑相关对象
 * @Date create in 2023/11/18 10:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixSiteEditResult implements Serializable {

    /**
     * 站点编号
     */
    private String serialNo;

    private Integer id;
}
