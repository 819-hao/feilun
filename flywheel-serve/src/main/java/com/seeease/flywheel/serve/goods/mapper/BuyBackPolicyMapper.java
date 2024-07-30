package com.seeease.flywheel.serve.goods.mapper;

import com.seeease.flywheel.serve.goods.entity.BuyBackPolicy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author edy
* @description 针对表【buy_back_policy(回购政策表)】的数据库操作Mapper
* @createDate 2023-03-17 16:21:07
* @Entity com.seeease.flywheel.serve.goods.entity.BuyBackPolicy
*/
public interface BuyBackPolicyMapper extends BaseMapper<BuyBackPolicy> {

    BuyBackPolicy selectByType(@Param("type") Integer type);
}




