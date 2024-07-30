package com.seeease.flywheel.serve.account.rpc;

import com.seeease.flywheel.account.ICostJdFlMappingFacade;
import com.seeease.flywheel.account.result.CostJdFlMappingResult;
import com.seeease.flywheel.serve.account.convert.CostJdFlMappingConverter;
import com.seeease.flywheel.serve.account.service.CostJdFlMappingService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/23 14:50
 */
@DubboService(version = "1.0.0")
public class CostJdFlMappingFacade implements ICostJdFlMappingFacade {

    @Resource
    private CostJdFlMappingService service;

    @Override
    public List<CostJdFlMappingResult> list() {
        return service.list().stream().map(shopCompanyMapping -> CostJdFlMappingConverter.INSTANCE.convertCostJdFlMappingResult(shopCompanyMapping)).collect(Collectors.toList());
    }
}
