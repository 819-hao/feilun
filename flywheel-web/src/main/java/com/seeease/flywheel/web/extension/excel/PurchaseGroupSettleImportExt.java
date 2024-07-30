package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.request.GroupSettleStockQueryImportRequest;
import com.seeease.flywheel.goods.result.GroupSettleStockQueryImportResult;
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

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 同行集采结算导入
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.PURCHASE_GROUP_SETTLE)
public class PurchaseGroupSettleImportExt implements ImportExtPtl<GroupSettleStockQueryImportRequest, GroupSettleStockQueryImportResult> {
    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade facade;

    @Override
    public Class<GroupSettleStockQueryImportRequest> getRequestClass() {
        return GroupSettleStockQueryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<GroupSettleStockQueryImportRequest> cmd) {

        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()) && cmd.getRequest().getDataList().stream().allMatch(Objects::nonNull), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(GroupSettleStockQueryImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream()
                .collect(Collectors.groupingBy(r -> r.getOriginSerialNo() + "#" + r.getStockSn()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getKey())
                .collect(Collectors.joining(","));

        if (StringUtils.isNotEmpty(repeatStockSn)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REPEAT, repeatStockSn);
        }
    }

    @Override
    public ImportResult<GroupSettleStockQueryImportResult> handle(ImportCmd<GroupSettleStockQueryImportRequest> cmd) {
        return facade.settleStockQueryImport(cmd.getRequest());
    }
}
