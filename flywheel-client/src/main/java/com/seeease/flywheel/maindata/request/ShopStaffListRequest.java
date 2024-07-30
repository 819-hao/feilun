package com.seeease.flywheel.maindata.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/5/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopStaffListRequest implements Serializable {

    /**
     * 店铺id
     */
    private Integer shopId;
    /**
     * 用户名
     */
    private String name;
}
