package com.seeease.flywheel.sf.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressOrderCreateResult implements Serializable {

    private Integer id;

    private String waybillNo;
}
