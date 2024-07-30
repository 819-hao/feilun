package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.entity.StoreQuota;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Tiro
 * @description 针对表【store(仓库表)】的数据库操作Service
 * @createDate 2023-03-07 19:29:21
 */
public interface StoreQuotaService extends IService<StoreQuota> {

    BigDecimal selectQuota(Integer id, Date date);
}
