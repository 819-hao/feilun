package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.pricing.request.ModelPriceChangeImportRequest;
import com.seeease.flywheel.pricing.result.ModelPriceChangeImportResult;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @Author Mr. Du
 * @Description 型号价格批量更改
 * @Date create in 2023/3/31 10:51
 */
@Slf4j
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.MODEL_PRICE_CHANGE)
public class ModelPriceChangeImportExt implements ImportExtPtl<ModelPriceChangeImportRequest, ModelPriceChangeImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade stockFacade;

    @Override
    public Class<ModelPriceChangeImportRequest> getRequestClass() {
        return ModelPriceChangeImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<ModelPriceChangeImportRequest> cmd) {
    }

    @Override
    public ImportResult<ModelPriceChangeImportResult> handle(ImportCmd<ModelPriceChangeImportRequest> cmd) {

        return stockFacade.modelPriceChange(cmd.getRequest());
    }
}
