package com.seeease.flywheel.serve.financial.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IFinancialTxHistoryFacade;
import com.seeease.flywheel.financial.request.TxHistoryDeleteRequest;
import com.seeease.flywheel.financial.request.TxHistoryImportRequest;
import com.seeease.flywheel.financial.request.TxHistoryQueryRequest;
import com.seeease.flywheel.financial.result.TxHistoryQueryResult;
import com.seeease.flywheel.serve.financial.convert.FinancialTxHistoryConvert;
import com.seeease.flywheel.serve.financial.entity.FinancialTxHistory;
import com.seeease.flywheel.serve.financial.service.FinancialTxHistoryService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@DubboService(version = "1.0.0")
public class FinancialTxHistoryFacade implements IFinancialTxHistoryFacade {

    @Resource
    private FinancialTxHistoryService financialTxHistoryService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importDate(TxHistoryImportRequest request) {
        request.getDataList().forEach(v -> {

            FinancialTxHistory record = FinancialTxHistoryConvert.INSTANCE.to(v);
            financialTxHistoryService.save(record);
        });
    }

    @Override
    public PageResult<TxHistoryQueryResult> page(TxHistoryQueryRequest request) {

        LambdaQueryWrapper<FinancialTxHistory> qw = Wrappers.<FinancialTxHistory>lambdaQuery()
                .between(StringUtils.isNotEmpty(request.getStartTime()) && StringUtils.isNotEmpty(request.getEndTime()),
                        FinancialTxHistory::getTxTime,
                        request.getStartTime(),
                        request.getEndTime())
                .like(StringUtils.isNotEmpty(request.getSerial()), FinancialTxHistory::getSerial, request.getSerial())
                .like(StringUtils.isNotEmpty(request.getPhone()), FinancialTxHistory::getPhone, request.getPhone())
                .like(StringUtils.isNotEmpty(request.getSellerName()), FinancialTxHistory::getSellerName, request.getSellerName())
                .like(StringUtils.isNotEmpty(request.getIdCard()), FinancialTxHistory::getIdCard, request.getIdCard())
                .like(StringUtils.isNotEmpty(request.getSn()), FinancialTxHistory::getSn, request.getSn())
                .orderByDesc(FinancialTxHistory::getSerial);

        Page<FinancialTxHistory> of = Page.of(request.getPage(), request.getLimit());
        financialTxHistoryService.page(of, qw);


        return PageResult.<TxHistoryQueryResult>builder()
                .totalCount(of.getTotal())
                .totalPage(of.getPages())
                .result(FinancialTxHistoryConvert.INSTANCE.to(of.getRecords()))
                .build();
    }

    @Override
    public void remove(TxHistoryDeleteRequest request) {

        if (CollectionUtils.isEmpty(request.getIdList())) {
            financialTxHistoryService.removeBatchByIds(financialTxHistoryService.list(Wrappers.<FinancialTxHistory>lambdaQuery().eq(FinancialTxHistory::getDeleted, WhetherEnum.NO))
                    .stream().filter(Objects::nonNull).map(FinancialTxHistory::getId).collect(Collectors.toList()));
            return;
        }
        financialTxHistoryService.removeBatchByIds(request.getIdList());
    }
}
