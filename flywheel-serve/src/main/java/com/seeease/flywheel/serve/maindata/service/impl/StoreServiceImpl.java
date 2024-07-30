package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.mapper.StoreMapper;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import org.springframework.stereotype.Service;

/**
 * @author Tiro
 * @description 针对表【store(仓库表)】的数据库操作Service实现
 * @createDate 2023-03-07 19:29:21
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store>
        implements StoreService {
    /**
     * 根据门店id查仓库
     *
     * @param shopId
     * @return
     */
    @Override
    public Store selectByShopId(Integer shopId) {
        return baseMapper.selectByShopId(shopId);
    }




}




