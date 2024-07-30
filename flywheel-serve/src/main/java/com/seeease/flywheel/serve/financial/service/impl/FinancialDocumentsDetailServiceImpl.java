package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.financial.request.FinancialDetailsRequest;
import com.seeease.flywheel.serve.financial.entity.FinancialDocumentsDetail;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsDetailService;
import com.seeease.flywheel.serve.financial.mapper.FinancialDocumentsDetailMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author edy
 * @description 针对表【financial_documents_detail(财务单据详情)】的数据库操作Service实现
 * @createDate 2023-03-27 09:52:56
 */
@Service
public class FinancialDocumentsDetailServiceImpl extends ServiceImpl<FinancialDocumentsDetailMapper, FinancialDocumentsDetail>
        implements FinancialDocumentsDetailService {

    @Override
    public List<FinancialDocumentsDetail> detail(FinancialDetailsRequest request) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<FinancialDocumentsDetail>()
                .eq(FinancialDocumentsDetail::getFinancialDocumentsId, request.getId()));
    }
}




