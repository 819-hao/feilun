package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.mapper.AccountStockRelationMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author edy
 * @description 针对表【account_stock_relation(付款/收款单 与商品 关联)】的数据库操作Service实现
 * @createDate 2023-09-14 10:56:32
 */
@Service
public class AccountStockRelationServiceImpl extends ServiceImpl<AccountStockRelationMapper, AccountStockRelation>
        implements AccountStockRelationService {

    @Override
    public List<AccountStockRelation> accountStockByArcIdList(Integer arcId) {
        LambdaQueryWrapper<AccountStockRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountStockRelation::getArcId, arcId);
        List<AccountStockRelation> accountStockRelationList = this.baseMapper.selectList(queryWrapper);
        return accountStockRelationList;
    }

    @Override
    public void AccountStockRelationAdd(AccountStockRelation accountStockRelation) {
        this.baseMapper.insert(accountStockRelation);
    }

    @Override
    public List<AccountStockRelation> selectByAfpIds(List<Integer> afpIds) {
        if (CollectionUtils.isEmpty(afpIds))
            return Collections.EMPTY_LIST;
        return this.baseMapper.selectByAfpIds(afpIds);
    }


}




