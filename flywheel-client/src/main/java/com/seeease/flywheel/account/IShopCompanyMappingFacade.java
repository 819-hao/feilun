package com.seeease.flywheel.account;

import com.seeease.flywheel.account.result.ShopCompanyMappingResult;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:16
 */

public interface IShopCompanyMappingFacade {

    /**
     * 全量匹配
     * @return
     */
    List<ShopCompanyMappingResult> list();
}
