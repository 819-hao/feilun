package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.account.IAccountFacade;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.AllocateStockQueryImportRequest;
import com.seeease.flywheel.allocate.request.ModelLiveScriptImportRequest;
import com.seeease.flywheel.allocate.result.AllocateStockQueryImportResult;
import com.seeease.flywheel.goods.IGoodsWatchFacade;
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

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * 调拨导入
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.MODEL_SCRIPT)
public class ModelLiveScriptImportExt implements ImportExtPtl<ModelLiveScriptImportRequest, Void> {

    @DubboReference(check = false, version = "1.0.0")
    private IGoodsWatchFacade goodsWatchFacade;


    @Override
    public Class<ModelLiveScriptImportRequest> getRequestClass() {
        return ModelLiveScriptImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<ModelLiveScriptImportRequest> cmd) {

    }

    @Override
    public ImportResult<Void> handle(ImportCmd<ModelLiveScriptImportRequest> cmd) {
        goodsWatchFacade.excelImport(cmd.getRequest().getDataList());
        return null;
    }


}
