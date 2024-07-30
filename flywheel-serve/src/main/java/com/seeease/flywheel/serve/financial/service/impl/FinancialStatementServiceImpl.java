package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.financial.request.FinancialStatementDetailsRequest;
import com.seeease.flywheel.financial.request.FinancialStatementMiniPageQueryRequest;
import com.seeease.flywheel.financial.request.FinancialStatementQueryAllRequest;
import com.seeease.flywheel.financial.result.FinancialStatementDetailsResult;
import com.seeease.flywheel.financial.result.FinancialStatementMiniPageQueryResult;
import com.seeease.flywheel.financial.result.FinancialStatementQueryAllResult;
import com.seeease.flywheel.serve.financial.entity.FinancialStatement;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.service.FinancialStatementService;
import com.seeease.flywheel.serve.financial.mapper.FinancialStatementMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author edy
 * @description 针对表【financial_statement(财务流水记录)】的数据库操作Service实现
 * @createDate 2023-09-01 10:16:08
 */
@Service
public class FinancialStatementServiceImpl extends ServiceImpl<FinancialStatementMapper, FinancialStatement>
        implements FinancialStatementService {

    @Override
    public Page<FinancialStatementQueryAllResult> queryAll(FinancialStatementQueryAllRequest request) {
        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAudit(List<Integer> ids, String auditDescription, String userName) {
        if (CollectionUtils.isEmpty(ids))
            return;
        ids.forEach(id -> {
            FinancialStatement statement = new FinancialStatement();
            statement.setId(id);
            statement.setStatus(FinancialStatusEnum.AUDITED);
            statement.setAuditDescription(auditDescription);
            statement.setWaitAuditPrice(BigDecimal.ZERO);
            this.baseMapper.updateById(statement);
        });
    }

    @Override
    public FinancialStatementDetailsResult detail(FinancialStatementDetailsRequest request) {
        return this.baseMapper.detail(request);
    }

    @Override
    public Page<FinancialStatementMiniPageQueryResult> miniPageQuery(FinancialStatementMiniPageQueryRequest request) {

        return this.baseMapper.miniPageQuery(new Page(request.getPage(), request.getLimit()), request);
    }

}




