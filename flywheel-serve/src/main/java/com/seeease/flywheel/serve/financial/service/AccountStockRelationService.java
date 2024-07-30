package com.seeease.flywheel.serve.financial.service;

import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author edy
 * @description 针对表【account_stock_relation(付款/收款单 与商品 关联)】的数据库操作Service
 * @createDate 2023-09-14 10:56:32
 */
public interface AccountStockRelationService extends IService<AccountStockRelation> {

    /**
     * 根据确认收款单查询商品信息
     *
     * @param arcId
     * @return
     */
    List<AccountStockRelation> accountStockByArcIdList(Integer arcId);

    /**
     * 新增商品关联关系
     *
     * @param accountStockRelation
     */
    void AccountStockRelationAdd(AccountStockRelation accountStockRelation);

    List<AccountStockRelation> selectByAfpIds(List<Integer> afpIds);

}
