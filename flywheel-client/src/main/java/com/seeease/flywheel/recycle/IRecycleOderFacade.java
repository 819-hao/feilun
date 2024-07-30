package com.seeease.flywheel.recycle;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.recycle.request.*;
import com.seeease.flywheel.recycle.result.*;
import lombok.NonNull;

import java.util.List;

/**
 * 回收、回购订单
 *
 * @Auther Gilbert
 * @Date 2023/9/1 09:49
 */
public interface IRecycleOderFacade {

    /**
     * 商城订单创建飞轮待确认单
     *
     * @param request
     * @return
     */
    RecycleOrderResult orderCreate(MarketRecycleOrderRequest request);

    /**
     * 查询列表明细页面。暂时给商城使用
     *
     * @param request
     * @return
     */
    BuyBackForSaleResult recycleForSaleDetail(RecycleOrderVerifyRequest request);

    /**
     * 分页查询列表信息
     *
     * @param request
     * @return
     */
    PageResult<RecyclingListResult> list(RecycleOrderListRequest request);

    /**
     * 更新状态枚举值
     *
     * @param id
     */
    void updateRecycleStatus(@NonNull Integer id);

//    /**
//     * 查询状态枚举值
//     *
//     * @return
//     */
//    RecycleStatusList statusList();

    /**
     * 客户上传打款信息
     *
     * @param request
     * @return
     */
    RecycleOrderResult uploadRemit(MarkektRecycleUserBankRequest request);

    /**
     * 返回组装行信息
     *
     * @return
     */
    BuyBackForLineResult detailLine(RecycleOrderVerifyRequest request);

    /**
     * 进行仅回收或置换保存操作(创建采购单或者销售单)
     *
     * @param request
     * @return
     */
    ReplacementOrRecycleCreateResult replacementOrRecycleCreate(ReplacementOrRecycleCreateRequest request);

    /**
     * 客户是否接受
     *
     * @param request
     * @return
     * @Override
     */
    RecycleOrderVerifyResult firstVerify(RecycleOrderVerifyRequest request);

    /**
     * 二次确认
     *
     * @param request
     * @return
     */
    RecycleOrderSecondVerifyResult secondVerify(RecycleOrderSecondVerifyRequest request);

    /**
     * 查询单号
     *
     * @param request
     * @return
     */
    RecycleOrderDetailsResult details(RecycleOrderDetailsRequest request);

    /**
     * 客户支付
     *
     * @param request
     * @return
     */
    RecycleOrderPayResult clientPay(MarkektRecyclePayRequest request);

    /**
     * 查询开启工作流信息
     *
     * @param request
     * @return
     */
    MarkektRecycleGetSaleProcessResult getStartSaleProcess(MarkektRecycleGetSaleProcessRequest request);

    /**
     * 是否做过回购单
     */
    Boolean buyBackExits(MarketRecycleOrderRequest request);

    /**
     * 客户取消
     *
     * @param request
     * @return
     */
    RecycleOrderClientCancelResult clientCancel(RecycleOrderClientCancelRequest request);

    /**
     * 第一次报价
     *
     * @param request
     * @return
     */
    RecycleOrderFirstOfferResult firstOffer(RecycleOrderFirstOfferRequest request);

    /**
     * 第二次报价
     *
     * @param request
     * @return
     */
    RecycleOrderSecondOfferResult secondOffer(RecycleOrderSecondOfferRequest request);

    /**
     * 商城查询回收列表明细
     */
    MallRecycleOrderDetailResult mallRecycleDetail(RecycleOrderDetailsRequest request);

    /**
     * 变更用户
     *
     * @param request
     * @return
     */
    RecycleReplaceUserResult replaceUser(RecycleReplaceUserRequest request);

    /**
     * 拦截销售
     * @param purchaseId
     * @return
     */
    List<MarkektRecycleGetSaleProcessResult> intercept(Integer purchaseId);

    Boolean protocolSync(ProtocolSyncRequest request);
}
