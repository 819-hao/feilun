package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.entity.StoreQuota;
import com.seeease.flywheel.serve.maindata.mapper.StoreMapper;
import com.seeease.flywheel.serve.maindata.mapper.StoreQuotaMapper;
import com.seeease.flywheel.serve.maindata.service.StoreQuotaService;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Tiro
 * @description 针对表【store(仓库表)】的数据库操作Service实现
 * @createDate 2023-03-07 19:29:21
 */
@Service
public class StoreQuotaServiceImpl extends ServiceImpl<StoreQuotaMapper, StoreQuota>
        implements StoreQuotaService {


    @Override
    public BigDecimal selectQuota(Integer id, Date date) {
        return getBaseMapper().selectQuota(id,date);
    }
}




