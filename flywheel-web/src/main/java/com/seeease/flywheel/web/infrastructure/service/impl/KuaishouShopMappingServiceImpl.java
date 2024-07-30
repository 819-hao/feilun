package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.KuaishouShopMapping;
import com.seeease.flywheel.web.infrastructure.mapper.KuaishouShopMappingMapper;
import com.seeease.flywheel.web.infrastructure.service.KuaishouShopMappingService;
import org.springframework.stereotype.Service;

/**
* @author dmmasxnmf
* @description 针对表【kuaishou_shop_mapping(快手门店映射表)】的数据库操作Service实现
* @createDate 2023-12-01 16:22:28
*/
@Service
public class KuaishouShopMappingServiceImpl extends ServiceImpl<KuaishouShopMappingMapper, KuaishouShopMapping>
    implements KuaishouShopMappingService {

    @Override
    public int countDecryptNumberByToDays(Long douYinShopId) {
        return 0;
    }
}




