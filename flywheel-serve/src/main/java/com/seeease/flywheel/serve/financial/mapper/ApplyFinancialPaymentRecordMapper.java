package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentRecordRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentRecordPageResult;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author edy
* @description 针对表【apply_financial_payment_record】的数据库操作Mapper
* @createDate 2023-02-27 16:11:04
* @Entity com.seeease.flywheel.serve.applyFinancialPayment.entity.ApplyFinancialPaymentRecord
*/
public interface ApplyFinancialPaymentRecordMapper extends BaseMapper<ApplyFinancialPaymentRecord> {

    Page<ApplyFinancialPaymentRecordPageResult> getPage(Page page, @Param("request") ApplyFinancialPaymentRecordRequest request);
}




