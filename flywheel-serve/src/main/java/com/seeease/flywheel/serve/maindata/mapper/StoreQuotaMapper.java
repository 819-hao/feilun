package com.seeease.flywheel.serve.maindata.mapper;

import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.entity.StoreQuota;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;


public interface StoreQuotaMapper extends SeeeaseMapper<StoreQuota> {

    BigDecimal selectQuota(@Param("smId") Integer id, @Param("now") Date now);
}




