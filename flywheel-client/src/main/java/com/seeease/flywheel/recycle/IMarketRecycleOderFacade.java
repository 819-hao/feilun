package com.seeease.flywheel.recycle;

import com.seeease.flywheel.recycle.request.*;
import com.seeease.flywheel.recycle.result.*;
import com.seeease.springframework.SingleResponse;

import java.util.List;

/**
 * 回收、回购订单
 *
 * @Auther Gilbert
 * @Date 2023/9/1 09:49
 */
public interface IMarketRecycleOderFacade {
    /**
     * 订单创建
     *
     * @param request
     * @return
     */
    SingleResponse<RecycleOrderResult> create(MarketRecycleOrderRequest request);

    /**
     * 客户上传银行卡信息
     *
     * @param request
     * @return
     */
    SingleResponse<RecycleOrderResult> uploadRemit(MarkektRecycleUserBankRequest request);

    /**
     * 客户已支付
     *
     * @param request
     * @return
     */
    SingleResponse<RecycleOrderPayResult> clientPay(MarkektRecyclePayRequest request);

    /**
     * 查询是否做过回购单
     */
    SingleResponse<Boolean> buyBackExits(MarketRecycleOrderRequest request);

    /**
     * 查询列表明细页面。暂时给商城使用
     * @param request
     * @return
     */
    SingleResponse<BuyBackForSaleResult> recycleForSaleDetail(RecycleOrderVerifyRequest request);

    /**
     * 查询回购列表信息
     */
    List<RecyclingListResult> buyBackList(RecycleOrderListRequest request);

    /**
     * 商城查询回收列表明细
     */
    SingleResponse<MallRecycleOrderDetailResult> recycleDetail(RecycleOrderDetailsRequest request);

    /**
     * 回收协议同步到打款单上
     */
    SingleResponse<Boolean> protocolSync(ProtocolSyncRequest request);
}
