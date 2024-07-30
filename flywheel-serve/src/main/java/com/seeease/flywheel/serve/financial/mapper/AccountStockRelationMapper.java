package com.seeease.flywheel.serve.financial.mapper;

import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author edy
* @description 针对表【account_stock_relation(付款/收款单 与商品 关联)】的数据库操作Mapper
* @createDate 2023-09-14 10:56:32
* @Entity com.seeease.flywheel.serve.financial.entity.AccountStockRelation
*/
public interface AccountStockRelationMapper extends BaseMapper<AccountStockRelation> {

    List<AccountStockRelation> selectByAfpIds(@Param("afpIds") List<Integer> afpIds);
}




