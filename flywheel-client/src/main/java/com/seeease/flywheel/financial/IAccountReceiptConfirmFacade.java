package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;

import java.util.List;

/**
 * 收款单相关接口
 */
public interface IAccountReceiptConfirmFacade {

    /**
     * 企业微信小程序---确认收款分页查询
     * 1、待核销---未绑定任何流水
     * 2、已核销---待绑定金额为0
     * 3、部分核销---带绑定金额大于0
     *
     * @return
     */
    PageResult<AccountReceiptConfirmMiniPageResult> accountReceiptConfirmPageQuery(AccountReceiptConfirmMiniPageRequest request);

    /**
     * 创建收款单
     *
     * @return
     */
    AccountReceiptConfirmAddResult accountReceiptConfirmCreate(AccountReceiptConfirmAddRequest request);


    /**
     * PC端-分页查询确认收款
     *
     * @return
     */
    PageResult<AccountReceiptConfirmPageResult> accountReceiptConfirmPCPageQuery(AccountReceiptConfirmPageRequest request);

    /**
     * 确认收款单详情
     *
     * @param request
     * @return
     */
    PageResult<AccountReceiptConfirmDetailResult> accountReceiptConfirmDetail(AccountReceiptConfirmDetailRequest request);

    /**
     * 商品详情
     *
     * @param request
     * @return
     */
    AccountReceiptConfirmGoodsDetailResult accountReceiptConfirmGoodsDetail(AccountReceiptConfirmGoodsDetailRequest request);

    /**
     * 确认收款单流水确认
     *
     * @param request
     */
    void accountReceStateUpdate(AccountReceiptConfirmFlowUpdateRequest request);


    AccountReceiptConfirmUpdateResult accountReceiptConfirmUpdate(AccountReceiptConfirmUpdateRequest request);

    AccountReceiptConfirmRejectedResult rejected(AccountReceiptConfirmRejectedRequest request);

    AccountReceiptConfirmConfirmReceiptResult confirmReceipt(AccountReceiptConfirmConfirmReceiptRequest request);

    List<AccountReceiptConfirmConfirmReceiptRequest.CheckConfirmVO> checkConfirmReceipt(AccountReceiptConfirmConfirmReceiptRequest request);

    void cancel(AccountReceiptConfirmCancelRequest request);

    /**
     * 新的 创建确认收款
     * @param request
     * @return
     */
    AccountReceiptConfirmAddResult accountReceiptConfirmCreate(AccountReceiptConfirmCreateRequest request);

    AccountReceiptConfirmMiniDetailResult detail(AccountReceiptConfirmDetailRequest request);

    List<AccountReceiptConfirmCollectionDetailsResult> collectionDetails(AccountReceiptConfirmCollectionDetailsRequest request);

    void batchAudit(AccountReceiptConfirmBatchAuditRequest request);
}
