package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.common.biz.buyBackPolicy.BuyBackPolicyBO;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.StringTools;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.BuyBackPolicyActivityMapper;
import com.seeease.flywheel.serve.goods.mapper.BuyBackPolicyDetailMapper;
import com.seeease.flywheel.serve.goods.mapper.BuyBackPolicyMapper;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.BuyBackPolicyService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.mapper.PurchaseSubjectMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.springframework.utils.BigDecimalUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【buy_back_policy(回购政策表)】的数据库操作Service实现
 * @createDate 2023-03-17 16:21:07
 */
@Service
public class BuyBackPolicyServiceImpl extends ServiceImpl<BuyBackPolicyMapper, BuyBackPolicy>
        implements BuyBackPolicyService {

    @Resource
    private BuyBackPolicyActivityMapper buyBackPolicyActivityMapper;
    @Resource
    private BuyBackPolicyDetailMapper buyBackPolicyDetailMapper;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private BillSaleOrderLineMapper billSaleOrderLineMapper;
    @Resource
    private PurchaseSubjectMapper purchaseSubjectMapper;


    /**
     * 获取商品回购政策
     *
     * @param vo
     * @return
     */
    @Override
    public List<com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper> getStockBuyBackPolicy(BuyBackPolicyStrategyPo vo) {
        // 过滤toc价
        if (Objects.isNull(vo.getTocPrice()) || BigDecimalUtil.eqZero(vo.getTocPrice())) {
            return Collections.EMPTY_LIST;
        }
        //固定活动
        BuyBackPolicyActivity activity = buyBackPolicyActivityMapper.selectByStockId(vo.getStockId(), 1);
        if (Objects.nonNull(activity)) {
            List<BuyBackPolicyDetail> list = buyBackPolicyDetailMapper.selectByBbpId(activity.getBbpId());
            return getBuyBackPolicyMapper(list);
        }
        //固定无活动
        if (exclusionTime(vo)) {
            return Collections.EMPTY_LIST;
        }

        //新表二手表
        int type = Optional.ofNullable(vo)
                .filter(t -> "S级/99新".equals(t.getFiness()))
                .map(t -> {
                    PurchaseSubject ps = purchaseSubjectMapper.selectById(t.getSourceSubjectId());
                    if (Objects.nonNull(ps.getWatchStatus()) && NumberUtils.INTEGER_ONE.intValue() == ps.getWatchStatus()) {
                        return NumberUtils.INTEGER_ZERO;
                    } else {
                        return NumberUtils.INTEGER_ONE;
                    }
                })
                .orElse(NumberUtils.INTEGER_ONE);


        //查回购政策
        BuyBackPolicy buyBackPolicy = baseMapper.selectByType(type);

        //排除政策
        if (Objects.isNull(buyBackPolicy) || excludeCondition(vo, buyBackPolicy)) {
            return Collections.EMPTY_LIST;
        }

        //过滤条件
        if (!filterCondition(vo, buyBackPolicy)) {
            return Collections.EMPTY_LIST;
        }

        Integer flag = (Optional.ofNullable(vo.getClinchPrice()).orElse(vo.getTocPrice())).compareTo(buyBackPolicy.getPriceThreshold()) == 1 ? 1 : 0;
        List<BuyBackPolicyDetail> list = buyBackPolicyDetailMapper.selectByBbpId(buyBackPolicy.getId())
                .stream().filter(b -> Objects.equals(b.getType(), flag))
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getBuyBackTime())))
                .collect(Collectors.toList());
        return getBuyBackPolicyMapper(list);
    }

    @Override
    public List<BuyBackPolicyInfo> getStockBuyBackPolicy(BuyBackPolicyBO buyBackPolicyBO) {
        try {
            return buyBackPolicyBO.getBuyBackPolicy().getInfoList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    private List<BuyBackPolicyDetailVo> getBuyBackPolicyDetailVoList(List<BuyBackPolicyDetail> list) {
        List<BuyBackPolicyDetailVo> finalList = new ArrayList<>();
        for (BuyBackPolicyDetail buyBackPolicyDetail : list) {
            BuyBackPolicyDetailVo buyBackPolicyDetailVo = new BuyBackPolicyDetailVo();
            buyBackPolicyDetailVo.setBuyBackTime(buyBackPolicyDetail.getBuyBackTime());
            buyBackPolicyDetailVo.setDiscount(buyBackPolicyDetail.getDiscount());
            buyBackPolicyDetailVo.setType(buyBackPolicyDetail.getType());
            buyBackPolicyDetailVo.setReplacementDiscounts(new BigDecimal("0.5"));
            finalList.add(buyBackPolicyDetailVo);
        }
        return finalList.stream().sorted(Comparator.comparing(BuyBackPolicyDetailVo::getBuyBackTime)).collect(Collectors.toList());
    }


    private List<com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper> getBuyBackPolicyMapper(List<BuyBackPolicyDetail> list) {
        List<com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper> finalList = new ArrayList<>();
        for (BuyBackPolicyDetail buyBackPolicyDetail : list) {
            com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper buyBackPolicyDetailVo = new com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper();
            buyBackPolicyDetailVo.setBuyBackTime(Integer.valueOf(buyBackPolicyDetail.getBuyBackTime()));
            buyBackPolicyDetailVo.setDiscount(buyBackPolicyDetail.getDiscount());
            buyBackPolicyDetailVo.setType(buyBackPolicyDetail.getType());
            buyBackPolicyDetailVo.setReplacementDiscounts(new BigDecimal("0.5"));
            finalList.add(buyBackPolicyDetailVo);
        }
        return finalList.stream().sorted(Comparator.comparing(com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper::getBuyBackTime)).collect(Collectors.toList());
    }

    /**
     * 排除条件
     *
     * @param vo
     * @param buyBackPolicy
     * @return
     */
    private boolean excludeCondition(BuyBackPolicyStrategyPo vo, BuyBackPolicy buyBackPolicy) {
        //判断供应商是否勾选 没勾选则不需要 接下来的判断
        // TODO 订金采购判断条件需要重新弄
        return (!StringTools.calibrationParameters(buyBackPolicy.getSuppliers(), vo.getCustomerId(), true) &&
                vo.getPurchasePrice().compareTo(vo.getPricePub().multiply(new BigDecimal("0.6"))) == 1) ||
                !StringTools.calibrationParameters(buyBackPolicy.getRepertoryResource(), vo.getStockSrc(), true) ||
//                (buyBackPolicy.getTotalCostToPublicPrice() == 1 && vo.getTotalPrice().compareTo(vo.getPricePub()) > 0) ||
                Objects.equals(BusinessBillTypeEnum.TH_CG_DJ.getValue(), Integer.valueOf(vo.getStockSrc())) ||
                Objects.equals(BusinessBillTypeEnum.TH_CG_QK.getValue(), Integer.valueOf(vo.getStockSrc())) ||
                Objects.equals(BusinessBillTypeEnum.TH_CG_DJTP.getValue(), Integer.valueOf(vo.getStockSrc())) ||
                DateUtils.checkOutTime(buyBackPolicy.getInsuranceCardTime(), vo.getWarrantyDate());
    }

    /**
     * 0:代表是没有回购政策
     *
     * @param vo
     * @return
     */
    private boolean exclusionTime(BuyBackPolicyStrategyPo vo) {
        BuyBackPolicyActivity buyBackPolicyActivity = buyBackPolicyActivityMapper.selectByStockId(vo.getStockId(), 0);
        if (buyBackPolicyActivity == null)
            return false;
        return DateUtils.isEffectiveDate(new Date(), buyBackPolicyActivity.getEffectiveStartTime(), buyBackPolicyActivity.getEffectiveEndTime());
    }

    /**
     * 过滤条件
     *
     * @param vo
     * @param buyBackPolicy
     * @return
     */
    private boolean filterCondition(BuyBackPolicyStrategyPo vo, BuyBackPolicy buyBackPolicy) {
        return (StringUtils.isEmpty(buyBackPolicy.getFineness()) || !StringTools.calibrationParameters(buyBackPolicy.getFineness(), vo.getFiness(), true))
                &&
                (StringUtils.isEmpty(buyBackPolicy.getProcuringEntity()) || !StringTools.calibrationParameters(buyBackPolicy.getProcuringEntity(), vo.getSourceSubjectId(), true));
    }
}




