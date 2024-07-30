package com.seeease.flywheel.sf.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressOrderEditRequest implements Serializable {

    private Integer id;

    private Integer expressState;

    private String errorMsg;

    private String expressNo;
}
