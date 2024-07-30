package com.seeease.flywheel.serve.maindata.rpc;


import com.seeease.flywheel.PageResult;

import com.seeease.flywheel.maindata.IFirmShopFacade;

import com.seeease.flywheel.maindata.request.FirmShopQueryRequest;
import com.seeease.flywheel.maindata.request.FirmShopSubmitRequest;

import com.seeease.flywheel.maindata.result.FirmShopQueryResult;

import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.service.FirmShopService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.Assert;

import javax.annotation.Resource;


@DubboService(version = "1.0.0")
public class FirmShopFacade implements IFirmShopFacade {

    @Resource
    private FirmShopService firmShopService;
    @Resource
    private TagService tagService;

    @Override
    public void submit(FirmShopSubmitRequest request) {
        if (request.getDeptId() != null){
            Tag tag = tagService.selectByStoreManagementId(request.getDeptId());
            Assert.notNull(tag,"tagName error");
            request.setDept(tag.getTagName());
        }
        firmShopService.submit(request);
    }

    @Override
    public void del(Integer id) {
        firmShopService.del(id);
    }

    @Override
    public PageResult<FirmShopQueryResult> page(FirmShopQueryRequest request) {
        return firmShopService.pageOf(request);
    }
}
