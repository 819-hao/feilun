package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.maindata.entity.ShopDto;
import com.seeease.flywheel.serve.maindata.entity.ShopMemberDto;
import com.seeease.flywheel.serve.maindata.entity.StoreManagement;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author dmmasxnmf
 * @description 针对表【store_management(门店管理)】的数据库操作Service
 * @createDate 2023-01-31 16:30:14
 */
public interface StoreManagementService extends IService<StoreManagement> {

    /**
     * 查门店信息
     *
     * @param ids
     * @return
     */
    List<StoreManagementInfo> selectInfoByIds(@Param("ids") List<Integer> ids);

    /**
     * @param id
     * @return
     */
    StoreManagementInfo selectInfoById(Integer id);

    /**
     * 所有门店id 和 门店名字
     */
    Map<Integer, String> getStoreMap();

    /**
     * 门店名字  和  所有门店id
     */
    Map<String, Integer> getStoreIdMap();

    /**
     * 商城同步门店信息
     *
     * @return
     */
    List<ShopDto> listShop();

    /**
     * 商城同步门店员工信息
     *
     * @param sidList
     * @return
     */
    List<ShopMemberDto> listShopMember(List<Integer> sidList, List<String> roleKeyList);


    StoreManagement selectByStoreId(Integer storeId);

    StoreManagement selectByShopId(Integer shopId);

    List<ShopDto> listShopByName(List<String> tagNameList);
}
