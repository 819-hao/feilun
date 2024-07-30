package com.seeease.flywheel.maindata.result;

import com.seeease.flywheel.maindata.entity.Shop;
import com.seeease.flywheel.maindata.entity.ShopMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopStoreQueryResult implements Serializable {
    private Integer id;
    private String name;
    private Integer storeId;
}
