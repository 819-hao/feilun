package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IGoodsExtFacade;
import com.seeease.flywheel.goods.request.GoodsWatchUpdateImportRequest;
import com.seeease.flywheel.goods.result.GoodsWatchUpdateImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author Tiro
 * @date 2024/1/24
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.UPDATE_GOODS_WATCH)
public class GoodsWatchUpdateImportExt implements ImportExtPtl<GoodsWatchUpdateImportRequest, GoodsWatchUpdateImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IGoodsExtFacade goodsExtFacade;

    @Override
    public Class<GoodsWatchUpdateImportRequest> getRequestClass() {
        return GoodsWatchUpdateImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<GoodsWatchUpdateImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        for (GoodsWatchUpdateImportRequest.ImportDto importDto : cmd.getRequest().getDataList()) {
            if (StringUtils.isBlank(importDto.getModelCode())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_CODE_REQUIRE_NON_NULL);
            }
        }
    }

    @Override
    public ImportResult<GoodsWatchUpdateImportResult> handle(ImportCmd<GoodsWatchUpdateImportRequest> cmd) {
        return goodsExtFacade.updateGoodsWatchImport(cmd.getRequest());
    }
}
