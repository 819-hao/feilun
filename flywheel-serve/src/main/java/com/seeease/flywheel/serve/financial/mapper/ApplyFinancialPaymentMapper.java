package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentQueryAllRequest;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentQueryByConditionRequest;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentQueryRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentPageAllResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentPageQueryByConditionResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentPageResult;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author edy
 * @description 针对表【apply_financial_payment】的数据库操作Mapper
 * @createDate 2023-02-27 16:06:51
 * @Entity com.seeease.flywheel.serve.applyFinancialPayment.entity.ApplyFinancialPayment
 */
public interface ApplyFinancialPaymentMapper extends SeeeaseMapper<ApplyFinancialPayment> {

    Page<ApplyFinancialPaymentPageResult> getPage(Page page, @Param("request") ApplyFinancialPaymentQueryRequest request);

    /**
     * 打款单列表记录
     *
     * @param page
     * @return
     */
    Page<ApplyFinancialPaymentPageAllResult> getPageAll(Page page, @Param("request") ApplyFinancialPaymentQueryAllRequest request);

    Page<ApplyFinancialPaymentPageQueryByConditionResult> queryByCondition(Page page, @Param("request") ApplyFinancialPaymentQueryByConditionRequest request);

    BigDecimal usedPrice(@Param("tagId") Integer tagId);

    BigDecimal usedOsQuota(@Param("shopId") Integer shopId,
                           @Param("sjId") Integer sjId,
                           @Param("bids") List<Integer> bids);

    BigDecimal usedCtQuota(@Param("sjId") Integer sjId,@Param("bids") List<Integer> bids);
}




