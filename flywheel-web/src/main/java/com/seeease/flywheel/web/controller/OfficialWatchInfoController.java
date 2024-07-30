package com.seeease.flywheel.web.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.web.entity.OfficialWatchInfo;
import com.seeease.flywheel.web.entity.request.OfficialWatchInfoListRequest;
import com.seeease.flywheel.web.infrastructure.service.OfficialWatchInfoService;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/6/30
 */
@Slf4j
@RestController
@RequestMapping("/officialWatchInfo")
public class OfficialWatchInfoController {
    @Resource
    private OfficialWatchInfoService officialWatchInfoService;

    /**
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody OfficialWatchInfoListRequest request) {

        Page<OfficialWatchInfo> res = officialWatchInfoService.page(Page.of(request.getPage(), request.getLimit()),
                Wrappers.<OfficialWatchInfo>lambdaQuery()
                        .orderByAsc(OfficialWatchInfo::getBrandName, OfficialWatchInfo::getSeriesName)
                        .like(StringUtils.isNotEmpty(request.getBrandName()), OfficialWatchInfo::getBrandName, request.getBrandName())
                        .like(StringUtils.isNotEmpty(request.getSeriesName()), OfficialWatchInfo::getSeriesName, request.getSeriesName())
                        .like(StringUtils.isNotEmpty(request.getModel()), OfficialWatchInfo::getModel, request.getModel())
                        .gt(OfficialWatchInfo::getPricePub, BigDecimal.ZERO)
        );

        return SingleResponse.of(PageResult.<OfficialWatchInfo>builder()
                .result(res.getRecords())
                .totalCount(res.getTotal())
                .totalPage(res.getPages())
                .build());
    }
}
