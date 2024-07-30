package com.seeease.flywheel.serve.maindata.rpc;

import com.google.common.collect.Lists;
import com.seeease.flywheel.maindata.IShopFacade;
import com.seeease.flywheel.maindata.entity.ShopMember;
import com.seeease.flywheel.maindata.request.ShopQueryRequest;
import com.seeease.flywheel.maindata.request.ShopStaffListRequest;
import com.seeease.flywheel.maindata.result.ShopQueryResult;
import com.seeease.flywheel.maindata.result.ShopStaffListResult;
import com.seeease.flywheel.maindata.result.ShopStoreQueryResult;
import com.seeease.flywheel.serve.maindata.convert.ShopConverter;
import com.seeease.flywheel.serve.maindata.entity.ShopDto;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/2/18
 */
@DubboService(version = "1.0.0")
public class ShopFacade implements IShopFacade {
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private UserService userService;

    /**
     * 查门店信息
     *
     * @param request
     * @return
     */
    @Override
    public ShopQueryResult query(ShopQueryRequest request) {
        request.setRoleKeyList(Lists.newArrayList("exclusiveAdvisor",
                "consultant",
                "shopclerk",
                "shopowner",
                "appletOperator",
                "zbOperator"));
        List<ShopDto> storeManagementList = storeManagementService.listShop();

        ShopQueryResult.ShopQueryResultBuilder builder = ShopQueryResult.builder()
                .shops(storeManagementList.stream()
                        .map(ShopConverter.INSTANCE::convert)
                        .collect(Collectors.toList()));
        if (request.isWhitMember() && CollectionUtils.isNotEmpty(request.getRoleKeyList())) {
            List<Integer> shopIds = storeManagementList.stream().map(ShopDto::getId).collect(Collectors.toList());
            builder.shopMembers(storeManagementService.listShopMember(shopIds, request.getRoleKeyList())
                    .stream()
                    .map(ShopConverter.INSTANCE::convert)
                    .collect(Collectors.groupingBy(ShopMember::getShopId)));
        }

        return builder.build();
    }

    @Override
    public List<ShopStaffListResult> staffList(ShopStaffListRequest request) {
        if (Objects.isNull(request.getShopId())) {
            return Collections.EMPTY_LIST;
        }
        return userService.listByShopAndName(request.getShopId(), request.getName())
                .stream()
                .map(ShopConverter.INSTANCE::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShopStoreQueryResult> listShopByName(List<String> tagNameList) {

        return storeManagementService.listShopByName(tagNameList).stream().map(ShopConverter.INSTANCE::convertStore).collect(Collectors.toList());
    }
}
