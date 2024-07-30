package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.DouYinShopMappingVO;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinShopMappingMapper;
import com.seeease.flywheel.web.infrastructure.service.DouYinShopMappingService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Tiro
 * @description 针对表【douyin_shop_mapping(抖音门店映射表)】的数据库操作Service实现
 * @createDate 2023-05-05 18:25:55
 */
@Service
public class DouYinShopMappingServiceImpl extends ServiceImpl<DouYinShopMappingMapper, DouYinShopMapping>
        implements DouYinShopMappingService {

    /**
     * @param douYinShopId
     * @param authorId
     * @return
     */
    @Override
    public DouYinShopMapping getByDouYinShopId(Long douYinShopId, Long authorId) {
        //优先匹配达人id的门店映射关系
        return Optional.ofNullable(baseMapper.selectOne(Wrappers.<DouYinShopMapping>lambdaQuery()
                        .eq(DouYinShopMapping::getDouYinShopId, douYinShopId)
                        .eq(DouYinShopMapping::getAuthorId, authorId)))
                .orElseGet(() -> Optional.ofNullable(authorId)
                        .filter(t -> t > 0) // 匹配默认抖音门店映射关系
                        .map(t -> baseMapper.selectOne(Wrappers.<DouYinShopMapping>lambdaQuery()
                                .eq(DouYinShopMapping::getDouYinShopId, douYinShopId)
                                .eq(DouYinShopMapping::getAuthorId, 0L)))
                        .orElse(null));
    }


    @Override
    public DouYinShopMappingVO getByDouYinOrderId(String orderId) {
        return baseMapper.getByOrder(orderId);
    }


    @Override
    public int countDecryptNumberByToDays(Long douYinShopId) {
        if (Objects.isNull(douYinShopId)) {
            return NumberUtils.INTEGER_ZERO;
        }
        return baseMapper.countDecryptNumberByToDays(douYinShopId);
    }
}




