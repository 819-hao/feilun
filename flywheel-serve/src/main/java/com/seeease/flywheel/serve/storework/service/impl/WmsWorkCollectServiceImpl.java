package com.seeease.flywheel.serve.storework.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.goods.entity.StockQuantityDTO;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPreExt;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCapacityDTO;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCollect;
import com.seeease.flywheel.serve.storework.enums.WmsWorkCollectWorkStateEnum;
import com.seeease.flywheel.serve.storework.enums.WmsWorkPrintExpressState;
import com.seeease.flywheel.serve.storework.mapper.WmsWorkCollectMapper;
import com.seeease.flywheel.serve.storework.service.WmsWorkCollectService;
import com.seeease.flywheel.storework.request.WmsWorkListRequest;
import com.seeease.flywheel.storework.result.WmsWorkCollectCountResult;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【wms_work_collect(发货作业集单表)】的数据库操作Service实现
 * @createDate 2023-08-31 17:58:28
 */
@Service
public class WmsWorkCollectServiceImpl extends ServiceImpl<WmsWorkCollectMapper, WmsWorkCollect>
        implements WmsWorkCollectService {
    @Resource
    private StockMapper stockMapper;

    @Override
    public Page<BillStoreWorkPreExt> waitWorkList(WmsWorkListRequest request) {
        return baseMapper.waitWorkList(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<BillStoreWorkPreExt> listWorkCollect(WmsWorkListRequest request) {
        return Page.<BillStoreWorkPreExt>of(request.getPage(), request.getLimit()).setRecords(baseMapper.listWorkCollect(request));
    }

    @Override
    public Page<BillStoreWorkPreExt> pageWorkCollect(WmsWorkListRequest request) {
        return baseMapper.pageWorkCollect(Page.of(request.getPage(), request.getLimit()), request);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectWork(Integer shopId, List<BillStoreWorkPre> billStoreWorkPreList) {
        baseMapper.insertBatchSomeColumn(billStoreWorkPreList.stream()
                .map(BillStoreWorkPre::getOriginSerialNo)
                .map(StringUtils::trim)
                .distinct()
                .map(t ->
                {
                    WmsWorkCollect collect = new WmsWorkCollect();
                    collect.setBelongingStoreId(shopId);
                    collect.setOriginSerialNo(t);
                    collect.setWorkState(WmsWorkCollectWorkStateEnum.WAIT_PRINT);
                    collect.setPrintExpressState(WmsWorkPrintExpressState.INIT);
                    return collect;
                }).collect(Collectors.toList()));
    }

    @Override
    public List<WmsWorkCollectCountResult> countByGroupModelAndSn(WmsWorkListRequest request) {
        return baseMapper.countByGroupModelAndSn(request);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCollectWorkState(List<WmsWorkCollect> collectList, WmsWorkCollectWorkStateEnum.TransitionEnum transitionEnum) {
        if (CollectionUtils.isEmpty(collectList)) {
            return;
        }
        collectList.stream()
                .forEach(t -> {
                    WmsWorkCollect up = new WmsWorkCollect();
                    up.setId(t.getId());
                    up.setPrintExpressState(t.getPrintExpressState());
                    up.setTransitionStateEnum(transitionEnum);
                    UpdateByIdCheckState.update(baseMapper, up);
                });
    }

    @Override
    public Map<Integer, WmsWorkCapacityDTO> inWorkStockCount(List<Integer> goodsIdList, Integer belongingStoreId) {

        Map<Integer, StockQuantityDTO> goodsMap = stockMapper.countSaleStockQuantity(goodsIdList, belongingStoreId)
                .stream()
                .collect(Collectors.toMap(StockQuantityDTO::getGoodsId, Function.identity()));

        Map<Integer, WmsWorkCapacityDTO> workMap = baseMapper.inWorkStockCount(belongingStoreId)
                .stream()
                .collect(Collectors.toMap(WmsWorkCapacityDTO::getGoodsId, Function.identity()));

        return goodsIdList.stream()
                .map(gid -> {
                    WmsWorkCapacityDTO dto = Optional.ofNullable(workMap.get(gid))
                            .orElseGet(() -> {
                                WmsWorkCapacityDTO d = new WmsWorkCapacityDTO();
                                d.setGoodsId(gid);
                                d.setInWorkQuantity(0);
                                return d;
                            });
                    StockQuantityDTO sqDto = goodsMap.get(gid);
                    dto.setModel(sqDto.getModel());
                    dto.setStockQuantity(sqDto.getStockQuantity());
                    return dto;
                })
                .collect(Collectors.toMap(WmsWorkCapacityDTO::getGoodsId, Function.identity()));
    }
}




