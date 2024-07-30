package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.maindata.entity.*;
import com.seeease.flywheel.serve.maindata.mapper.StoreManagementMapper;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【store_management(门店管理)】的数据库操作Service实现
 * @createDate 2023-01-31 16:30:14
 */
@Service
public class StoreManagementServiceImpl extends ServiceImpl<StoreManagementMapper, StoreManagement>
        implements StoreManagementService {

    @Override
    public List<StoreManagementInfo> selectInfoByIds(List<Integer> ids) {
        return baseMapper.selectByIds(ids);
    }

    @Override
    public StoreManagementInfo selectInfoById(Integer id) {
        return Optional.ofNullable(id)
                .map(i -> baseMapper.selectByIds(Lists.newArrayList(i)))
                .filter(CollectionUtils::isNotEmpty)
                .map(t -> t.stream().findFirst().orElse(null))
                .orElse(null);
    }

    @Override
    public Map<Integer, String> getStoreMap() {
        return baseMapper.selectAllList().stream().collect(Collectors.toMap(StoreManagementForTag::getId, StoreManagementForTag::getName));
    }

    @Override
    public Map<String, Integer> getStoreIdMap() {
        return baseMapper.selectAllList().stream().collect(Collectors.toMap(StoreManagementForTag::getName, StoreManagementForTag::getId));
    }

    /**
     * 商城同步门店信息
     *
     * @return
     */
    @Override
    public List<ShopDto> listShop() {
        return baseMapper.listShop();
    }

    /**
     * 商城同步门店员工信息
     *
     * @param sidList
     * @return
     */
    @Override
    public List<ShopMemberDto> listShopMember(List<Integer> sidList, List<String> roleKeyList) {
        return baseMapper.listShopMember(sidList, roleKeyList);
    }

    @Override
    public StoreManagement selectByStoreId(Integer storeId) {
        return baseMapper.selectByStoreId(storeId);
    }

    @Override
    public StoreManagement selectByShopId(Integer shopId) {
        return baseMapper.selectByShopId(shopId);
    }
    @Override
    public List<ShopDto> listShopByName(List<String> tagNameList){
        return baseMapper.listShopByName(tagNameList);
    }

}




