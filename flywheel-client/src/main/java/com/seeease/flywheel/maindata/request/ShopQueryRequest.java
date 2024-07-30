package com.seeease.flywheel.maindata.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopQueryRequest implements Serializable {
    /**
     * 是否需要店铺成员信息
     */
    private boolean whitMember;
    /**
     * 需要的角色
     */
    List<String> roleKeyList;
}
