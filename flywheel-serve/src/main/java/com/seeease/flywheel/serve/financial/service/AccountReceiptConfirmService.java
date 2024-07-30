package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmDetailResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmMiniPageResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmPageResult;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;

import java.util.List;


/**
 * 确认收款接口
 */
public interface AccountReceiptConfirmService extends IService<AccountReceiptConfirm> {

    AccountReceiptConfirm accountReceiptConfirmAdd(AccountReceiptConfirmAddRequest request);

    Page<AccountReceiptConfirmMiniPageResult> accountReceiptConfirmMiniPageQuery(AccountReceiptConfirmMiniPageRequest request);

    Page<AccountReceiptConfirmPageResult> accountReceiptConfirmPageQuery(AccountReceiptConfirmPageRequest request);

    List<Integer> accountReceiptConfirmStateUpdate(AccountReceiptConfirmFlowUpdateRequest request);

    List<AccountReceiptConfirmDetailResult> accountReceiptConfirmDetail(AccountReceiptConfirmDetailRequest request);

    AccountReceiptConfirm accountReceiptConfirmQueryById(Integer id);

    void confirmReceipt(AccountReceiptConfirmConfirmReceiptRequest request);

    AccountReceiptConfirm accountReceiptConfirmCreate(AccountReceiptConfirmCreateRequest request);
}
