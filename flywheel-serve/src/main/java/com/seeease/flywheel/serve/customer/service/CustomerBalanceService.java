package com.seeease.flywheel.serve.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.customer.request.CustomerBalancePageRequest;
import com.seeease.flywheel.customer.result.CustomerBalancePageResult;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户余额相关接口
 */
public interface CustomerBalanceService {

    /**
     * 查看一个公司下的联系人---余额信息
     *
     * @param customerId
     * @param contractsIds
     * @return
     */
    List<CustomerBalance> customerBalanceList(Integer customerId, List<Integer> contractsIds);

    /**
     * 查询寄售货值
     * @param customerId
     * @return
     */
    List<BillSaleOrder> consignmentGoodsQuery(Integer customerId);

    Page<CustomerBalancePageResult> customerBalancePage(CustomerBalancePageRequest request);

    /**
     * 销售单流水记录
     *
     * @param customerId
     * @param contractsId
     * @param amount      金额
     * @param cmdType     CustomerBalanceCmdTypeEnum
     */
    void customerBalanceCmd(Integer customerId, Integer contractsId, BigDecimal amount, Integer type, Integer shopId, Integer cmdType, Integer createId, String originSerialNo);

    /**
     * 指定结算人
     * @param customerId
     * @param contractsId
     * @param amount
     * @param type
     * @param shopId
     * @param cmdType
     * @param createId
     */
    void customerBalanceByCreateIdCmd(Integer customerId, Integer contractsId, BigDecimal amount, Integer type, Integer shopId, Integer cmdType,Integer createId);
}
