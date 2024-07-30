package com.seeease.flywheel.maindata;

import com.seeease.flywheel.maindata.request.ShopQueryRequest;
import com.seeease.flywheel.maindata.request.ShopStaffListRequest;
import com.seeease.flywheel.maindata.result.ShopQueryResult;
import com.seeease.flywheel.maindata.result.ShopStaffListResult;
import com.seeease.flywheel.maindata.result.ShopStoreQueryResult;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/17
 */
public interface IShopFacade {

    /**
     * 查询门店信息
     *
     * @param request
     * @return
     */
    ShopQueryResult query(ShopQueryRequest request);

    /**
     * 店铺员工
     *
     * @param request
     * @return
     */
    List<ShopStaffListResult> staffList(ShopStaffListRequest request);

    /**
     * 查询门店和仓库信息
     * @param tagNameList
     * @return
     */
    List<ShopStoreQueryResult> listShopByName(List<String> tagNameList);
}
