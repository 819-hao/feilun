package com.seeease.flywheel.serve.goods.mapper;

import com.seeease.flywheel.serve.goods.entity.BuyBackPolicyDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author edy
* @description 针对表【buy_back_policy_detail(回购政策详情表)】的数据库操作Mapper
* @createDate 2023-03-17 16:21:07
* @Entity com.seeease.flywheel.serve.goods.entity.BuyBackPolicyDetail
*/
public interface BuyBackPolicyDetailMapper extends BaseMapper<BuyBackPolicyDetail> {

    List<BuyBackPolicyDetail> selectByBbpId(@Param("bbpId") Integer bbpId);

}




