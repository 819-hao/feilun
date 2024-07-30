package com.seeease.flywheel.customer.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankCreateResult implements Serializable {

    private Integer bankId;
}
