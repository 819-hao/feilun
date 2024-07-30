package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 销售工作流挂载参数
 * @Date create in 2023/9/7 15:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleLoadResult implements Serializable {

    private Boolean success;

    private String msg;
}
