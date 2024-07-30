package com.seeease.flywheel.web.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IFinancialInvoiceFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.FinancialInvoiceCreateResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.FinancialInvoiceNotice;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 申请开票
 *
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@RestController
@RequestMapping("/financialInvoice")
public class FinancialInvoiceController {
    @DubboReference(check = false, version = "1.0.0")
    private IFinancialInvoiceFacade facade;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @NacosValue(value = "${invoice.belong:1,22}", autoRefreshed = true)
    private List<Integer> belongId;

    /**
     * pc创建申请开票
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/create")
    public SingleResponse create(@RequestBody FinancialInvoiceCreateRequest request) {
        Assert.notNull(request, "数据不能为空");

        //特殊逻辑
        if (request.getLines().stream().allMatch(r -> belongId.contains(r.getBelongId()))) {
            //不做特殊控制
            log.warn("经营权=" + JSON.toJSONString(request.getLines()));
        } else {
            Assert.isTrue(request.getLines()
                    .stream()
                    .map(FinancialInvoiceCreateRequest.LineDto::getBelongId)
                    .collect(Collectors.toSet()).size() == 1, "商品归属必须一致");
        }

        FinancialInvoiceCreateResult result = facade.create(request);

        //通知财务审核 打款单
        FinancialInvoiceNotice notice = new FinancialInvoiceNotice();
        notice.setId(result.getId());
        notice.setSerialNo(result.getSerialNo());
        notice.setCreatedBy(UserContext.getUser().getUserName());
        notice.setCreatedTime(new Date());
        notice.setState(FlywheelConstant.PENDING_INVOICE);
        notice.setShopId(FlywheelConstant._ZB_ID);
        notice.setToUserRoleKey(Lists.newArrayList(FlywheelConstant.HQ_FINANCE));
        wxCpMessageFacade.send(notice);

        return SingleResponse.of(result);
    }

    /**
     * 小程序修改开票
     *
     * @param request
     * @return
     */
    @PostMapping("/wx/edit")
    public SingleResponse update(@RequestBody FinancialInvoiceUpdateRequest request) {

        facade.update(request);

        return SingleResponse.buildSuccess();
    }

    /**
     * 小程序取消开票
     *
     * @param request
     * @return
     */
    @PostMapping("/wx/cancel")
    public SingleResponse cancel(@RequestBody FinancialInvoiceCancelRequest request) {

        facade.cancel(request);

        return SingleResponse.buildSuccess();
    }

    /**
     * 小程序查询列表
     *
     * @param request
     * @return
     */
    @PostMapping("/wx/query")
    public SingleResponse query(@RequestBody FinancialInvoiceQueryRequest request) {

        return SingleResponse.of(facade.query(request));
    }

    /**
     * 小程序商品信息
     *
     * @param request
     * @return
     */
    @PostMapping("/wx/stockInfos")
    public SingleResponse stockInfos(@RequestBody FinancialInvoiceStockInfosRequest request) {

        return SingleResponse.of(facade.stockInfos(request));
    }

    /**
     * pc、小程序 详情 两端共用
     *
     * @param request
     * @return
     */
    @PostMapping("/detail")
    public SingleResponse detail(@RequestBody FinancialInvoiceDetailRequest request) {

        return SingleResponse.of(facade.detail(request));
    }

    /**
     * 小程序查询审核列表
     *
     * @param request
     * @return
     */
    @PostMapping("/wx/approvedMemo")
    public SingleResponse approvedMemo(@RequestBody FinancialInvoiceRecordRequest request) {

        return SingleResponse.of(facade.approvedMemo(request));
    }

    /**
     * pc端查询列表
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/queryByCondition")
    public SingleResponse queryByCondition(@RequestBody FinancialInvoiceQueryByConditionRequest request) {

        return SingleResponse.of(facade.queryByCondition(request));
    }

    /**
     * 导出
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/export")
    public SingleResponse export(@RequestBody FinancialInvoiceQueryByConditionRequest request) {

        return SingleResponse.of(facade.export(request));
    }

    /**
     * 查询采购主体
     *
     * @return
     */
    @PostMapping("/queryInvoiceSubject")
    public SingleResponse queryInvoiceSubject() {

        return SingleResponse.of(facade.queryInvoiceSubject());
    }

    /**
     * 上传发票
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/uploadInvoice")
    public SingleResponse uploadInvoice(@RequestBody FinancialInvoiceUploadInvoiceRequest request) {
        facade.uploadInvoice(request);
        return SingleResponse.buildSuccess();
    }
}
