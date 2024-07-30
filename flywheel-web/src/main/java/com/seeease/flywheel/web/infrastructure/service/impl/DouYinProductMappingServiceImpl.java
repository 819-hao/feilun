package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seeease.flywheel.web.entity.DouYinProductMapping;
import com.seeease.flywheel.web.entity.DouYinProductMappingVO;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinProductMappingMapper;
import com.seeease.flywheel.web.infrastructure.service.DouYinProductMappingService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【douyin_product_mapping(抖音商品关系)】的数据库操作Service实现
 * @createDate 2023-07-20 14:50:56
 */
@Service
public class DouYinProductMappingServiceImpl extends ServiceImpl<DouYinProductMappingMapper, DouYinProductMapping>
        implements DouYinProductMappingService {

    @Override
    public List<DouYinProductMappingVO> selectProductStock(Integer shopId) {
        List<Integer> subjectId = baseMapper.selectSubject(Lists.newArrayList(shopId, 1));
        return baseMapper.selectProductStock(shopId, subjectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateMapping(List<DouYinProductMapping> mappingList) {
        if (CollectionUtils.isEmpty(mappingList)) {
            return;
        }
        //删除历史
        baseMapper.deleteBySku(mappingList.get(0).getShopId(), mappingList.stream()
                .map(DouYinProductMapping::getDouYinSkuId)
                .collect(Collectors.toList()));
        //批量插入
        baseMapper.insertBatchSomeColumn(mappingList);

    }
}




