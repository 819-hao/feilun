package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.maindata.request.FirmShopQueryRequest;
import com.seeease.flywheel.maindata.request.FirmShopSubmitRequest;
import com.seeease.flywheel.maindata.result.FirmShopQueryResult;
import com.seeease.flywheel.serve.maindata.entity.FirmShop;
import com.seeease.flywheel.serve.maindata.entity.Store;

/**
 * @author Tiro
 * @description 针对表【store(仓库表)】的数据库操作Service
 * @createDate 2023-03-07 19:29:21
 */
public interface FirmShopService extends IService<FirmShop> {


    void submit(FirmShopSubmitRequest request);

    void del(Integer id);

    PageResult<FirmShopQueryResult> pageOf(FirmShopQueryRequest request);
}
