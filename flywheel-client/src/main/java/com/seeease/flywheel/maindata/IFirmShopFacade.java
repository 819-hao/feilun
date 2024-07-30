package com.seeease.flywheel.maindata;


import com.seeease.flywheel.PageResult;

import com.seeease.flywheel.maindata.request.FirmShopQueryRequest;
import com.seeease.flywheel.maindata.request.FirmShopSubmitRequest;

import com.seeease.flywheel.maindata.result.FirmShopQueryResult;



public interface IFirmShopFacade {


    void submit(FirmShopSubmitRequest request);

    void del(Integer id);

    PageResult<FirmShopQueryResult> page(FirmShopQueryRequest request);
}
