package com.seeease.flywheel.serve.pricing.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.pricing.request.ApplyPricingAuditorRequest;
import com.seeease.flywheel.pricing.result.ApplyPricingAuditorResult;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.goods.entity.BillLifeCycle;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.BillLifeCycleMapper;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.pricing.convert.PricingConvert;
import com.seeease.flywheel.serve.pricing.entity.BillApplyPricing;
import com.seeease.flywheel.serve.pricing.entity.BillPricing;
import com.seeease.flywheel.serve.pricing.entity.LogPricingOpt;
import com.seeease.flywheel.serve.pricing.enums.ApplyPricingStateEnum;
import com.seeease.flywheel.serve.pricing.enums.PricingNodeEnum;
import com.seeease.flywheel.serve.pricing.mapper.BillApplyPricingMapper;
import com.seeease.flywheel.serve.pricing.mapper.BillPricingMapper;
import com.seeease.flywheel.serve.pricing.mapper.LogPricingOptMapper;
import com.seeease.flywheel.serve.pricing.service.BillApplyPricingService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author Tiro
 * @description 针对表【bill_apply_pricing(调价申请)】的数据库操作Service实现
 * @createDate 2024-02-22 18:28:11
 */
@Slf4j
@Service
public class BillApplyPricingServiceImpl extends ServiceImpl<BillApplyPricingMapper, BillApplyPricing>
        implements BillApplyPricingService {
    @Resource
    private BillPricingMapper billPricingMapper;
    @Resource
    private LogPricingOptMapper logPricingOptMapper;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private BillLifeCycleMapper billLifeCycleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplyPricingAuditorResult auditor(ApplyPricingAuditorRequest request) {
        BillApplyPricing up = new BillApplyPricing();
        up.setId(request.getId());
        up.setAuditor(request.getAuditor());
        up.setApprovedTime(new Date());

        switch (ApplyPricingStateEnum.fromCode(request.getApplyStatus())) {
            case PASS:
                BillApplyPricing billApplyPricing = baseMapper.selectById(request.getId());

                Stock stock = stockMapper.selectById(billApplyPricing.getStockId());
                if (StockStatusEnum.SOLD_OUT.equals(stock.getStockStatus())) {
                    up.setTransitionStateEnum(ApplyPricingStateEnum.TransitionEnum.REJECTION);
                    up.setRejectionReason(OperationExceptionCode.APPLY_PRICING_SYSTEM_REJECTION.getErrMsg());
                    UpdateByIdCheckState.update(baseMapper, up);
                    return new ApplyPricingAuditorResult(request.getId(), true);
                }

                up.setTransitionStateEnum(ApplyPricingStateEnum.TransitionEnum.PASS);
                up.setApprovedTocPrice(Objects.requireNonNull(request.getApprovedTocPrice()));
                up.setApprovedTagPrice(request.getApprovedTocPrice().add(SeeeaseConstant.TAG_PRICE_ROLE_MAP.get(request.getApprovedTocPrice())));
                UpdateByIdCheckState.update(baseMapper, up);
                //更新定价单价格
                BillPricing bp = billPricingMapper.selectOne(Wrappers.<BillPricing>lambdaQuery().eq(BillPricing::getStockId, billApplyPricing.getStockId()));
                BillPricing upPricing = new BillPricing();
                upPricing.setId(bp.getId());
                upPricing.setCPrice(up.getApprovedTocPrice());
                upPricing.setTPrice(up.getApprovedTagPrice());
                upPricing.setCMargin(upPricing.getCPrice()
                        .subtract(bp.getAllPrice()).divide(upPricing.getCPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
                billPricingMapper.updateById(upPricing);
                //日志
                LogPricingOpt logPricingOpt = PricingConvert.INSTANCE.convertLogPricingOpt(bp);
                logPricingOpt.setPricingNode(PricingNodeEnum.CHECK);
                logPricingOpt.setCPrice(upPricing.getCPrice());
                logPricingOpt.setTPrice(upPricing.getTPrice());
                logPricingOpt.setCMargin(upPricing.getCMargin());
                logPricingOptMapper.insert(logPricingOpt);
                //更新商品价格
                Stock upStock = new Stock();
                upStock.setId(billApplyPricing.getStockId());
                upStock.setTocPrice(upPricing.getCPrice());
                upStock.setTagPrice(upPricing.getTPrice());
                //改价格
                stockMapper.updateById(upStock);

                //补充生命周期
                try {
                    BillLifeCycle billLifeCycle = new BillLifeCycle();
                    billLifeCycle.setStockId(billApplyPricing.getStockId());
                    billLifeCycle.setOriginSerialNo(billApplyPricing.getSerialNo());
                    billLifeCycle.setOperationDesc("调价申请-审核通过");
                    billLifeCycle.setOperationTime(System.currentTimeMillis());
                    billLifeCycleMapper.insert(billLifeCycle);
                } catch (Exception e) {
                    log.error("审核申请定价补充生命周期异常，billApplyPricing={}", billApplyPricing);
                }

                break;
            case REJECTION:
                up.setTransitionStateEnum(ApplyPricingStateEnum.TransitionEnum.REJECTION);
                up.setRejectionReason(request.getRejectionReason());
                UpdateByIdCheckState.update(baseMapper, up);
                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }

        return new ApplyPricingAuditorResult(request.getId(), false);
    }
}




