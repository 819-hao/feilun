package com.seeease.flywheel.serve.maindata.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.maindata.request.FirmShopQueryRequest;
import com.seeease.flywheel.maindata.request.FirmShopSubmitRequest;
import com.seeease.flywheel.maindata.result.FirmShopQueryResult;
import com.seeease.flywheel.serve.maindata.convert.FirmShopConverter;
import com.seeease.flywheel.serve.maindata.entity.FirmShop;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.mapper.FirmShopMapper;
import com.seeease.flywheel.serve.maindata.mapper.StoreMapper;
import com.seeease.flywheel.serve.maindata.service.FirmShopService;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【store(仓库表)】的数据库操作Service实现
 * @createDate 2023-03-07 19:29:21
 */
@Service
@Slf4j
public class FirmShopServiceImpl extends ServiceImpl<FirmShopMapper, FirmShop>
        implements FirmShopService {



    @Override
    public void submit(FirmShopSubmitRequest request) {
        saveOrUpdate(FirmShopConverter.INSTANCE.to(request));
    }

    @Override
    public void del(Integer id) {
        removeById(id);
    }

    @Override
    public PageResult<FirmShopQueryResult> pageOf(FirmShopQueryRequest request) {
        Page<FirmShop> pageRet = Page.of(request.getPage(), request.getLimit());
        LambdaQueryWrapper<FirmShop> qw = Wrappers.<FirmShop>lambdaQuery()
                .eq(StringUtils.isNotEmpty(request.getHfMemberId()), FirmShop::getHfMemberId, request.getHfMemberId())
                .like(StringUtils.isNotEmpty(request.getFirmName()), FirmShop::getFirmName, request.getFirmName());



        page(pageRet,qw);

        return PageResult.<FirmShopQueryResult>builder()
                .result(pageRet.getRecords().stream().map(FirmShopConverter.INSTANCE::to).collect(Collectors.toList()))
                .totalPage(pageRet.getPages())
                .totalCount(pageRet.getTotal())
                .build();
    }
}




