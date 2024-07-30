package com.seeease.flywheel.serve.rfid.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.rfid.IRfidFacade;
import com.seeease.flywheel.rfid.result.RfidConfigResult;
import com.seeease.flywheel.rfid.result.RfidOutStoreListResult;
import com.seeease.flywheel.rfid.result.RfidShopReceiveListResult;
import com.seeease.flywheel.rfid.result.RfidWorkDetailResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.rfid.entity.StoreShop;
import com.seeease.flywheel.serve.rfid.mapper.StoreShopMapper;
import com.seeease.flywheel.serve.rfid.service.RfidConfigService;
import com.seeease.flywheel.serve.storework.convert.BillStoreWorkPreConvert;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkStateEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.StoreWorkOutStorageRfidDetailRequest;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.Assert;


import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0")
@Slf4j
public class rfidFacade implements IRfidFacade {
    @Resource
    private RfidConfigService rfidConfigService;
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private StockService stockService;
    @Resource
    private StoreShopMapper storeShopMapper;



    @Override
    public RfidConfigResult config(Integer platform) {
        return rfidConfigService.config(platform);
    }


    @Override
    public List<RfidOutStoreListResult> rfidWaitOutStoreList(Integer shopId, String q,List<String> brandNameList) {
        Integer storeId = UserContext.getUser().getStore().getId();
        if (shopId == FlywheelConstant._ZB_ID) {
            Assert.isTrue(storeId == FlywheelConstant._ZB_ID, OperationExceptionCode.RFID_NO_AUTH.getErrMsg());
        } else {
            shopId = storeId;
        }
        Integer goodsId = null;
        if (q.startsWith("XYW")) {
            goodsId = stockService.list(Wrappers.<Stock>lambdaQuery().eq(Stock::getWno, q))
                    .stream()
                    .findFirst()
                    .map(Stock::getGoodsId)
                    .orElse(null);
        }
        return billStoreWorkPreService.rfidWaitOutStoreList(shopId, q, goodsId,brandNameList);
    }

    @Override
    public List<RfidShopReceiveListResult> rfidWaitReceiveList(String q) {
        Integer storeId = UserContext.getUser().getStore().getId();
        if (storeId == FlywheelConstant._ZB_ID) {
            return Collections.emptyList();
        }

        Integer goodsId = null;
        if (q.startsWith("XYW")) {
            goodsId = stockService.list(Wrappers.<Stock>lambdaQuery().eq(Stock::getWno, q))
                    .stream()
                    .findFirst()
                    .map(Stock::getGoodsId)
                    .orElse(null);
        }
        return billStoreWorkPreService.rfidWaitReceiveList(storeId, q, goodsId);
    }

    @Override
    public Integer queryStoreIdByShopId(Integer shopId) {
        StoreShop storeShop = storeShopMapper.selectOne(Wrappers.<StoreShop>lambdaQuery().eq(StoreShop::getDelted, 0).eq(StoreShop::getShopId, shopId).last("limit 1"));
        return storeShop == null ? null : storeShop.getStoreId();
    }


}
