package com.seeease.flywheel.serve.maindata.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.fix.request.FixSiteCreateRequest;
import com.seeease.flywheel.fix.request.FixSiteDetailsRequest;
import com.seeease.flywheel.fix.request.FixSiteEditRequest;
import com.seeease.flywheel.fix.request.FixSiteListRequest;
import com.seeease.flywheel.fix.result.FixSiteCreateResult;
import com.seeease.flywheel.fix.result.FixSiteDetailsResult;
import com.seeease.flywheel.fix.result.FixSiteEditResult;
import com.seeease.flywheel.fix.result.FixSiteListResult;
import com.seeease.flywheel.maindata.IFixSiteFacade;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.maindata.convert.FixSiteConverter;
import com.seeease.flywheel.serve.maindata.entity.FixSite;
import com.seeease.flywheel.serve.maindata.enums.TagTypeEnum;
import com.seeease.flywheel.serve.maindata.service.FixSiteService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/11/18 10:28
 */
@DubboService(version = "1.0.0")
public class FixSiteFacade implements IFixSiteFacade {

    @Resource
    private FixSiteService fixSiteService;

    @Resource
    private StoreManagementService storeManagementService;

    @Override
    public FixSiteCreateResult create(FixSiteCreateRequest request) {

        if (TagTypeEnum.UNDEFINED == TagTypeEnum.fromValue(request.getTagType())) {
            request.setSerialNo(SerialNoGenerator.generateFixSiteTagSerialNo());

            FixSite fixSite = FixSiteConverter.INSTANCE.convert(request);
            fixSiteService.save(fixSite);
            return FixSiteConverter.INSTANCE.convertFixSiteCreateResult(fixSite);
        } else if (TagTypeEnum.CREATE == TagTypeEnum.fromValue(request.getTagType())) {
            request.setSerialNo(SerialNoGenerator.generateFixSiteSerialNo());

            //关联查询
            Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getParentFixSiteId()) || StringUtils.isNotBlank(t.getParentFixSiteSerialNo()))
                    .map(t -> fixSiteService.getOne(Wrappers.<FixSite>lambdaQuery()
                            .eq(FixSite::getId, t.getParentFixSiteId())
                            .or().eq(FixSite::getSerialNo, t.getParentFixSiteSerialNo())))
                    .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_SITE_TAG_NOT_EXIT));

            FixSite fixSite = FixSiteConverter.INSTANCE.convert(request);
            fixSiteService.save(fixSite);
            return FixSiteConverter.INSTANCE.convertFixSiteCreateResult(fixSite);
        }
        throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
    }

    @Override
    public FixSiteEditResult edit(FixSiteEditRequest request) {

        FixSite fixSite = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> fixSiteService.getOne(Wrappers.<FixSite>lambdaQuery()
                        .eq(FixSite::getId, t.getId())
                        .or().eq(FixSite::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_SITE_NOT_EXIT));

        request.setId(fixSite.getId());
        fixSiteService.updateById(FixSiteConverter.INSTANCE.convert(request));

        return FixSiteConverter.INSTANCE.convertFixSiteEditResult(fixSite);
    }

    @Override
    public FixSiteDetailsResult details(FixSiteDetailsRequest request) {

        FixSite fixSite = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> fixSiteService.getOne(Wrappers.<FixSite>lambdaQuery()
                        .eq(FixSite::getId, t.getId())
                        .or().eq(FixSite::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_SITE_NOT_EXIT));

        return FixSiteConverter.INSTANCE.convertFixSiteDetailsResult(fixSite);
    }

    @Override
    public PageResult<FixSiteListResult> list(FixSiteListRequest request) {

        LambdaQueryWrapper<FixSite> wrapper = Wrappers.<FixSite>lambdaQuery()
                .eq(Objects.nonNull(request.getTagType()), FixSite::getTagType, request.getTagType())
                .between(StringUtils.isNotBlank(request.getStartTime()) && StringUtils.isNotBlank(request.getEndTime()), FixSite::getCreatedTime, request.getStartTime(), request.getEndTime())
                .eq(Objects.nonNull(request.getSiteState()), FixSite::getSiteState, request.getSiteState())
                .eq(StringUtils.isNotBlank(request.getSiteName()), FixSite::getSiteName, request.getSiteName())

                .orderByDesc(FixSite::getId);

        if (Objects.nonNull(request) && (Objects.nonNull(request.getParentFixSiteId()) || StringUtils.isNotBlank(request.getParentFixSiteSerialNo()))) {
            Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getParentFixSiteId()) || StringUtils.isNotBlank(t.getParentFixSiteSerialNo()))
                    .map(t -> fixSiteService.getOne(Wrappers.<FixSite>lambdaQuery()
                            .eq(FixSite::getId, t.getParentFixSiteId())
                            .or().eq(FixSite::getSerialNo, t.getParentFixSiteSerialNo())))
                    .filter(Objects::nonNull)
                    .map(t -> {
                        wrapper.eq(FixSite::getParentFixSiteId, t.getId());
                        return t;
                    })
                    .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_SITE_TAG_NOT_EXIT));
        }

        Page<FixSite> page = fixSiteService.page(new Page<>(request.getPage(), request.getLimit()), wrapper);

        return PageResult.<FixSiteListResult>builder()
                .result(CollectionUtils.isEmpty(page.getRecords()) ? Arrays.asList() :
                        page.getRecords().stream().map(FixSiteConverter.INSTANCE::convertFixSiteListResult)
                                .map(i -> {
                                    i.setOriginStoreName(Objects.nonNull(i.getOriginStoreId()) ? storeManagementService.selectInfoById(i.getOriginStoreId()).getName() : "");
                                    return i;
                                })
                                .collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

}
