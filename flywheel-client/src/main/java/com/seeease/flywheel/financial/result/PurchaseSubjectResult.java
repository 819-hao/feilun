package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/10/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSubjectResult implements Serializable {

    private Integer id;

    private String name;

}
