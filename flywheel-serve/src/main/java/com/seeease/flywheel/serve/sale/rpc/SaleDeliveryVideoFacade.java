package com.seeease.flywheel.serve.sale.rpc;

import com.seeease.flywheel.sale.ISaleDeliveryVideoFacade;
import com.seeease.flywheel.sale.request.SaleDeliveryVideoRequest;
import com.seeease.flywheel.sale.result.SaleDeliveryVideoResult;
import com.seeease.flywheel.serve.sale.convert.RcSaleDeliveryVideoConverter;
import com.seeease.flywheel.serve.sale.entity.RcSaleDeliveryVideo;
import com.seeease.flywheel.serve.sale.service.RcSaleDeliveryVideoService;
import com.seeease.springframework.context.UserContext;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@DubboService(version = "1.0.0")
public class SaleDeliveryVideoFacade implements ISaleDeliveryVideoFacade {
    @Resource
    private RcSaleDeliveryVideoService rcSaleDeliveryVideoService;

    @Override
    public SaleDeliveryVideoResult save(SaleDeliveryVideoRequest request) {
        RcSaleDeliveryVideo saleDeliveryVideo = RcSaleDeliveryVideoConverter.INSTANCE.convert(request);
        saleDeliveryVideo.setBelongingStoreId(UserContext.getUser().getStore().getId());
        rcSaleDeliveryVideoService.save(saleDeliveryVideo);
        return SaleDeliveryVideoResult.builder()
                .id(saleDeliveryVideo.getId())
                .build();
    }
}
