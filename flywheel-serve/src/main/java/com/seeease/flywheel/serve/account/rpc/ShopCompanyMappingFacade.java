package com.seeease.flywheel.serve.account.rpc;

import com.seeease.flywheel.account.IShopCompanyMappingFacade;
import com.seeease.flywheel.account.result.ShopCompanyMappingResult;
import com.seeease.flywheel.serve.account.convert.ShopCompanyMappingConverter;
import com.seeease.flywheel.serve.account.service.ShopCompanyMappingService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:31
 */
@DubboService(version = "1.0.0")
public class ShopCompanyMappingFacade implements IShopCompanyMappingFacade {

    @Resource
    private ShopCompanyMappingService service;

    @Override
    public List<ShopCompanyMappingResult> list() {
        return service.list().stream().map(shopCompanyMapping -> ShopCompanyMappingConverter.INSTANCE.convertShopCompanyMappingResult(shopCompanyMapping)).collect(Collectors.toList());
    }
}
