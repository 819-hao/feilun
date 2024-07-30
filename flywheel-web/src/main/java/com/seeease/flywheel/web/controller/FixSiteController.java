package com.seeease.flywheel.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.seeease.flywheel.fix.request.FixSiteCreateRequest;
import com.seeease.flywheel.fix.request.FixSiteDetailsRequest;
import com.seeease.flywheel.fix.request.FixSiteEditRequest;
import com.seeease.flywheel.fix.request.FixSiteListRequest;
import com.seeease.flywheel.maindata.IFixSiteFacade;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author wbh
 * @date 2023/3/1
 * 维修站点
 */
@Slf4j
@RestController
@RequestMapping("/fixSite")
public class FixSiteController {


    @DubboReference(check = false, version = "1.0.0")
    private IFixSiteFacade fixSiteFacade;

    /**
     * 站点
     *
     * @param request
     * @return
     */
    @PostMapping("/createBySite")
    public SingleResponse createBySite(@RequestBody FixSiteCreateRequest request) {
        request.setTagType(1);
        request.setSiteType(0);
        request.setSiteState(1);
        //断言
        Assert.isTrue(Objects.nonNull(request.getParentFixSiteId()) || StringUtils.isNotBlank(request.getParentFixSiteSerialNo()), "站点等级不能为空");
        Assert.isTrue(Objects.nonNull(request.getOriginStoreId()), "关联标签不能为空");
        Assert.isTrue(StringUtils.isNotBlank(request.getSiteName()), "站点名称不能为空");
        Assert.isTrue(StringUtils.isNotBlank(request.getSitePhone()), "站点电话不能为空");
        Assert.isTrue(StringUtils.isNotBlank(request.getSiteAddress()), "站点地址不能为空");

        return SingleResponse.of(fixSiteFacade.create(request));
    }

    /**
     * 等级
     *
     * @param request
     * @return
     */
    @PostMapping("/createByTag")
    public SingleResponse createByTag(@RequestBody FixSiteCreateRequest request) {
        request.setTagType(0);
        request.setSiteState(1);
        Assert.isTrue(StringUtils.isNotBlank(request.getSiteName()), "等级名称不能为空");
        return SingleResponse.of(fixSiteFacade.create(request));
    }

    /**
     * 详情
     *
     * @param request
     * @return
     */
    @PostMapping("/detailsBySite")
    public SingleResponse detailsBySite(@RequestBody FixSiteDetailsRequest request) {
        Assert.isTrue(Objects.nonNull(request.getId()) || StringUtils.isNotBlank(request.getSerialNo()), "id不能为空");
        return SingleResponse.of(fixSiteFacade.details(request));
    }

    /**
     * 站点
     *
     * @param request
     * @return
     */
    @PostMapping("/editBySite")
    public SingleResponse editBySite(@RequestBody FixSiteEditRequest request) {

        //断言
        Assert.isTrue(Objects.nonNull(request.getId()) || StringUtils.isNotBlank(request.getSerialNo()), "站点不能为空");
        Assert.isTrue(Objects.nonNull(request.getSiteState()), "站点状态不能为空");
        Assert.isTrue(StringUtils.isNotBlank(request.getSiteName()), "站点名称不能为空");
        Assert.isTrue(StringUtils.isNotBlank(request.getSitePhone()), "站点电话不能为空");
        Assert.isTrue(StringUtils.isNotBlank(request.getSiteAddress()), "站点地址不能为空");

        return SingleResponse.of(fixSiteFacade.edit(request));
    }

    /**
     * 等级
     *
     * @param request
     * @return
     */
    @PostMapping("/editByTag")
    public SingleResponse editByTag(@RequestBody FixSiteEditRequest request) {
        Assert.isTrue(Objects.nonNull(request.getId()) || StringUtils.isNotBlank(request.getSerialNo()), "等级不能为空");
        Assert.isTrue(StringUtils.isNotBlank(request.getSiteName()), "等级名称不能为空");
        return SingleResponse.of(fixSiteFacade.edit(request));
    }

    /**
     * 站点
     *
     * @param request
     * @return
     */
    @PostMapping("/listBySite")
    public SingleResponse listBySite(@RequestBody FixSiteListRequest request) {
        request.setTagType(1);
        return SingleResponse.of(fixSiteFacade.list(request));
    }

    /**
     * 等级
     *
     * @param request
     * @return
     */
    @PostMapping("/listByTag")
    public SingleResponse listByTag(@RequestBody FixSiteListRequest request) {
        request.setTagType(0);
        return SingleResponse.of(fixSiteFacade.list(request));
    }

}
