package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.common.biz.buyBackPolicy.BuyBackPolicyBO;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.serve.goods.entity.BuyBackPolicy;
import com.seeease.flywheel.serve.goods.entity.BuyBackPolicyStrategyPo;
import com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper;

import java.util.List;

/**
 * @author edy
 * @description 针对表【buy_back_policy(回购政策表)】的数据库操作Service
 * @createDate 2023-03-17 16:21:07
 */
public interface BuyBackPolicyService extends IService<BuyBackPolicy> {
    /**
     * @param vo
     * @return
     */
    @Deprecated
    List<BuyBackPolicyMapper> getStockBuyBackPolicy(BuyBackPolicyStrategyPo vo);

    /**
     * 新回购政策
     *
     * @param buyBackPolicyBO
     * @return
     */
    List<BuyBackPolicyInfo> getStockBuyBackPolicy(BuyBackPolicyBO buyBackPolicyBO);
}
