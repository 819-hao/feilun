package com.seeease.flywheel.maindata.result;

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
public class ShopStaffListResult implements Serializable {
    /**
     * 员工(用户)id
     */
    private Long staffId;
    /**
     * 员工(用户)名称
     */
    private String staffName;
}
