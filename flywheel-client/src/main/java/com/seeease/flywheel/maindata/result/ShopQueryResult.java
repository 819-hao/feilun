package com.seeease.flywheel.maindata.result;

import com.seeease.flywheel.maindata.entity.Shop;
import com.seeease.flywheel.maindata.entity.ShopMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopQueryResult implements Serializable {
    /**
     * 店铺
     */
    List<Shop> shops;
    /**
     * 店铺成员
     */
    Map<Integer/**店铺id**/, List<ShopMember>> shopMembers;
}
