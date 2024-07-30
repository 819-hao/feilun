package com.seeease.flywheel.web.controller;


import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.IBreakPriceAuditFacade;
import com.seeease.flywheel.helper.IBusinessCustomerAuditFacade;
import com.seeease.flywheel.helper.IMarketTrendsFacade;
import com.seeease.flywheel.helper.notice.BusinessCustomerAuditNotice;
import com.seeease.flywheel.helper.request.*;
import com.seeease.flywheel.helper.result.MarketTrendsDetailResult;
import com.seeease.flywheel.helper.result.MarketTrendsSearchResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;


import com.seeease.frameworkai.gpt.ChatRequest;
import com.seeease.frameworkai.gpt.ChatResult;
import com.seeease.frameworkai.gpt.LocalChatGptApi;
import com.seeease.springframework.SingleResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;

/**
 * 飞轮小程序控制器
 */
@RestController
@RequestMapping("helper")
public class FlyWheelHelperController {

    @DubboReference(check = false, version = "1.0.0")
    private IMarketTrendsFacade facade;
    @DubboReference(check = false, version = "1.0.0")
    private IBusinessCustomerAuditFacade businessCustomerAuditFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IBreakPriceAuditFacade breakPriceAuditFacade;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;
    @Resource
    private LocalChatGptApi chatGptApi;

    /**
     * 市场行情搜索
     * <a href = "https://lanhuapp.com/web/#/item/project/stage?tid=5b218e66-6661-4183-b1bc-f339b95504e1&pid=7c8315f9-a24e-40d9-a650-47d9953e3ee2">原型图</a>
     */
    @GetMapping("/market/trends/search")
    public SingleResponse<PageResult<MarketTrendsSearchResult>> pageMarketTrends(@RequestParam(defaultValue = "1") Integer page,
                                                                                 @RequestParam(defaultValue = "10") Integer limit,
                                                                                 @RequestParam(required = false) String q,
                                                                                 @RequestParam(required = false) String model) {
        return SingleResponse.of(facade.search(page, limit, q, model));
    }

    /**
     * ai图片型号匹配
     * <a href = "https://lanhuapp.com/web/#/item/project/stage?tid=5b218e66-6661-4183-b1bc-f339b95504e1&pid=7c8315f9-a24e-40d9-a650-47d9953e3ee2">原型图</a>
     */
    @PostMapping("/market/trends/ai/match")
    public SingleResponse<String> aiModelMatch(@RequestParam("file") MultipartFile file) throws IOException {
        return SingleResponse.of(facade.aiModelMatch(file.getBytes()));
    }


    /**
     * 市场行情详情
     * <a href = "https://lanhuapp.com/web/#/item/project/stage?tid=5b218e66-6661-4183-b1bc-f339b95504e1&pid=7c8315f9-a24e-40d9-a650-47d9953e3ee2">原型图</a>
     *
     * @param id
     * @param timeRange
     */
    @GetMapping("/market/trends/detail")
    public SingleResponse<MarketTrendsDetailResult> markTrendsDetail(@RequestParam Integer id,
                                                                     @RequestParam Integer timeRange) {
        return SingleResponse.of(facade.detail(id, timeRange));
    }


    /**
     * 企业客户审核创建
     * <a href="https://mastergo.com/file/104652803800550?page_id=M&shareId=104652803800550">原型图</a>
     *
     * @return
     */
    @PostMapping("/business/customer/audit/submit")
    public SingleResponse businessCustomerCreate(@RequestBody BusinessCustomerAuditCreateRequest request) {
        Assert.notNull(request, "请求不能为空");
        Assert.notNull(request.getFirmName(), "公司名称不能为空");
        Assert.notNull(request.getContactName(), "联系人名称不能为空");

        Integer id = businessCustomerAuditFacade.submit(request);
        //提交审核
        BusinessCustomerAuditNotice notice = new BusinessCustomerAuditNotice();
        notice.setId(id);
        notice.setToUserRoleKey(Collections.singletonList("purchaserLeader"));
        wxCpMessageFacade.send(notice);
        return SingleResponse.buildSuccess();
    }

    /**
     * 企业客户审核列表
     * <a href="https://mastergo.com/file/104652803800550?page_id=M&shareId=104652803800550">原型图</a>
     *
     * @return
     */
    @PostMapping("/business/customer/list")
    public SingleResponse businessCustomer(@RequestBody BusinessCustomerListRequest request) {
        Assert.notNull(request, "参数不能为空");
        return SingleResponse.of(businessCustomerAuditFacade.page(request));
    }

    /**
     * 企业客户审核
     * <a href="https://mastergo.com/file/104652803800550?page_id=M&shareId=104652803800550">原型图</a>
     *
     * @return
     */
    @PostMapping("/business/customer/audit")
    public SingleResponse businessCustomerAudit(@RequestBody BusinessCustomerAuditRequest request) {
        Assert.notNull(request, "请求不能为空");
        Assert.notNull(request.getId(), "请求id不能为空");
        Assert.notNull(request.getStatus(), "审核状态不能为空");
        businessCustomerAuditFacade.audit(request);
        return SingleResponse.buildSuccess();
    }


    /**
     * 破价审核列表 + 详情
     * <a href="https://x25v72.axshare.com/#id=hpgk2i&p=%E8%AF%A6%E6%83%85_1&g=1">原型</a>
     *
     * @param request
     * @return
     */
    @PostMapping("/break/price/audit/list")
    public SingleResponse breakPricePage(@RequestBody BreakPriceAuditPageRequest request) {
        return SingleResponse.of(breakPriceAuditFacade.pageOf(request));
    }

    /**
     * 破价审核
     * <a href="https://x25v72.axshare.com/#id=hpgk2i&p=%E8%AF%A6%E6%83%85_1&g=1">原型</a>
     *
     * @param request
     * @return
     */
    @PostMapping("/break/price/audit")
    public SingleResponse breakPriceAudit(@RequestBody BreakPriceAuditRequest request) {
        Assert.notNull(request, "参数不能为空");
        Assert.notEmpty(request.getIds(), "id列表不能为空");
        breakPriceAuditFacade.audit(request);
        return SingleResponse.buildSuccess();
    }


    /**
     * 破价历史记录
     * <a href="https://x25v72.axshare.com/#id=hpgk2i&p=%E8%AF%A6%E6%83%85_1&g=1">原型</a>
     *
     * @param id
     * @return
     */
    @GetMapping("/break/price/audit/history")
    public SingleResponse breakPriceAudit(@RequestParam Integer id) {
        Assert.notNull(id, "参数不能为空");
        return SingleResponse.of(breakPriceAuditFacade.history(id));
    }


    /**
     * 破价创建
     * <a href="https://x25v72.axshare.com/#id=hpgk2i&p=%E8%AF%A6%E6%83%85_1&g=1">原型</a>
     *
     * @param request
     * @return
     */
    @PostMapping("/break/price/audit/submit")
    public SingleResponse breakPriceSubmit(@RequestBody BreakPriceAuditSubmitRequest request) {
        return SingleResponse.of(breakPriceAuditFacade.submit(request));
    }


    @PostMapping("gpt/chat")
    public SingleResponse<ChatResult> gptChat(@RequestBody ChatRequest request) throws IOException {
        return SingleResponse.of(chatGptApi.chat(request).execute().body());
    }


}
