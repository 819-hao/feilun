package com.seeease.flywheel.serve.financial.service;

import com.seeease.flywheel.financial.request.FinancialDetailsRequest;
import com.seeease.flywheel.serve.financial.entity.FinancialDocumentsDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author edy
* @description 针对表【financial_documents_detail(财务单据详情)】的数据库操作Service
* @createDate 2023-03-27 09:52:56
*/
public interface FinancialDocumentsDetailService extends IService<FinancialDocumentsDetail> {

    List<FinancialDocumentsDetail> detail(FinancialDetailsRequest request);
}
