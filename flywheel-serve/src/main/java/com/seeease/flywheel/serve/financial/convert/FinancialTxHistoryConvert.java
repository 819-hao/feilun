package com.seeease.flywheel.serve.financial.convert;

import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentDetailResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentPageAllResult;
import com.seeease.flywheel.financial.result.TxHistoryQueryResult;
import com.seeease.flywheel.purchase.result.PurchaseApplySettlementResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.entity.FinancialTxHistory;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FinancialTxHistoryConvert extends EnumConvert {

    FinancialTxHistoryConvert INSTANCE = Mappers.getMapper(FinancialTxHistoryConvert.class);

    FinancialTxHistory to(TxHistoryImportRequest.ImportDto request);

    TxHistoryQueryResult to(FinancialTxHistory records);

    List<TxHistoryQueryResult> to(List<FinancialTxHistory> records);
}
