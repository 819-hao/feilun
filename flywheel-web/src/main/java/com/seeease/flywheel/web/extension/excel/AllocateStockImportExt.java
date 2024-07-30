package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.AllocateStockImportRequest;
import com.seeease.flywheel.allocate.result.AllocateStockBaseInfoImportResult;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.maindata.IShopFacade;
import com.seeease.flywheel.maindata.result.ShopStoreQueryResult;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther Gilbert
 * @Date 2023/11/7 14:35
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.ALLOCATE_STOCK)
public class AllocateStockImportExt implements ImportExtPtl<AllocateStockImportRequest, AllocateStockBaseInfoImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IAllocateFacade allocateFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade stockFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IShopFacade shopFacade;

    @Override
    public Class<AllocateStockImportRequest> getRequestClass() {
        return AllocateStockImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<AllocateStockImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(AllocateStockImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(AllocateStockImportRequest.ImportDto::getToStore)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.TO_STORE_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(AllocateStockImportRequest.ImportDto::getStockSn))
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
    public ImportResult<AllocateStockBaseInfoImportResult> handle(ImportCmd<AllocateStockImportRequest> cmd) {
        //查询导入表身号
        List<String> snList = cmd.getRequest().getDataList().stream().map(m->m.getStockSn().trim()).collect(Collectors.toList());
        //查询导入调入方
        List<String> toStoreList = cmd.getRequest().getDataList().stream().map(m->m.getToStore().trim()).collect(Collectors.toList());
        //判断导入的商品是否在可选择列表里面
        List<StockBaseInfo> stockBaseInfos = stockFacade.listStockByStockSnList(StockListRequest.builder()
                .stockSnList(snList)
                .build());
        if(CollectionUtils.isEmpty(stockBaseInfos)){
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_STOCK_SN);
        }
        //查询出的list转还map
        Map<String, StockBaseInfo> stringStockBaseInfoMap = stockBaseInfos.stream().collect(Collectors.toMap(StockBaseInfo::getStockSn, a -> a, (k1, k2) -> k1));
        //取交集的补集
        List<String> errSn = CollectionUtils.disjunction(snList, stringStockBaseInfoMap.keySet().stream().collect(Collectors.toList()))
                .stream().collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(errSn)){
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_ERROR,errSn);
        }
        List<ShopStoreQueryResult> shopStoreQueryResults = shopFacade.listShopByName(toStoreList);
        Map<String, ShopStoreQueryResult> stringShopStoreQueryResultMap = shopStoreQueryResults.stream().collect(Collectors.toMap(ShopStoreQueryResult::getName, a -> a, (k1, k2) -> k1));
        cmd.getRequest().getDataList().forEach(t->{
            ShopStoreQueryResult shopStoreQueryResult = stringShopStoreQueryResultMap.get(t.getToStore());
            if(Objects.nonNull(shopStoreQueryResult)){
                t.setToId(shopStoreQueryResult.getId());
                t.setToStoreId(shopStoreQueryResult.getStoreId());
            }
        });
        return allocateFacade.allocateStockImport(cmd.getRequest(),stockBaseInfos);
    }
}
