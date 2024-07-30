package com.seeease.flywheel.account.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCompanyMappingResult implements Serializable {

    private Integer id;

    private String shopGroup;

    private String shopName;

    private String companyName;

    private String department;
}
