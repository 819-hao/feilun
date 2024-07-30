package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.FinancialInvoiceDetailResult;
import com.seeease.flywheel.financial.result.FinancialInvoicePageResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceQueryByConditionResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceRecordPageResult;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.mapper.CustomerContactsMapper;
import com.seeease.flywheel.serve.customer.mapper.CustomerMapper;
import com.seeease.flywheel.serve.financial.convert.FinancialInvoiceConvert;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoice;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.financial.enums.InvoiceOriginEnum;
import com.seeease.flywheel.serve.financial.mapper.FinancialInvoiceMapper;
import com.seeease.flywheel.serve.financial.mapper.FinancialInvoiceRecordMapper;
import com.seeease.flywheel.serve.financial.mapper.FinancialInvoiceStockMapper;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceService;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.mapper.GoodsWatchMapper;
import com.seeease.flywheel.serve.maindata.mapper.PurchaseSubjectMapper;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderLineMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【financial_invoice】的数据库操作Service实现
 * @createDate 2023-10-19 09:35:12
 */
@Slf4j
@Service
public class FinancialInvoiceServiceImpl extends ServiceImpl<FinancialInvoiceMapper, FinancialInvoice>
        implements FinancialInvoiceService {

    @Resource
    private FinancialInvoiceRecordMapper invoiceRecordMapper;
    @Resource
    private FinancialInvoiceStockMapper invoiceStockMapper;
    @Resource
    private GoodsWatchMapper goodsWatchMapper;
    @Resource
    private BillSaleReturnOrderLineMapper saleReturnOrderLineMapper;
    @Resource
    private BillSaleOrderLineMapper saleOrderLineMapper;
    @Resource
    private PurchaseSubjectMapper purchaseSubjectMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private CustomerContactsMapper contactsMapper;

    @Override
    public Page<FinancialInvoicePageResult> queryPage(FinancialInvoiceQueryRequest request) {
        return this.baseMapper.queryPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public void update(FinancialInvoiceUpdateRequest request) {
        FinancialInvoice invoice = FinancialInvoiceConvert.INSTANCE.convertUpdate(request);
        invoice.setState(FinancialInvoiceStateEnum.PENDING_INVOICED);
        this.baseMapper.updateById(invoice);
    }

    @Override
    public FinancialInvoiceDetailResult detail(FinancialInvoiceDetailRequest request) {
        FinancialInvoiceDetailResult result = FinancialInvoiceConvert.INSTANCE
                .convertDetail(this.baseMapper.selectById(request.getId()));
        if (Objects.isNull(result)) {
            return null;
        }

//        List<FinancialInvoiceRecord> list = invoiceRecordMapper
//                .selectList(new LambdaQueryWrapper<FinancialInvoiceRecord>()
//                        .eq(FinancialInvoiceRecord::getFinancialInvoiceId, request.getId())
//                        .orderByDesc(FinancialInvoiceRecord::getId));
//        if (list.size() > 0)
//            result.setResult(list.get(0).getResult());

        List<FinancialInvoiceStock> financialInvoiceStocks = invoiceStockMapper.selectList(new LambdaQueryWrapper<FinancialInvoiceStock>()
                .eq(FinancialInvoiceStock::getFinancialInvoiceId, request.getId()));
        List<WatchDataFusion> fusionList = goodsWatchMapper.queryByStockIdList(financialInvoiceStocks.stream()
                .map(FinancialInvoiceStock::getStockId)
                .collect(Collectors.toList()));
        Map<Integer, WatchDataFusion> watchDataFusionMap = fusionList
                .stream().collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));

        result.setInvoiceSubjectName(purchaseSubjectMapper.selectNameById(result.getInvoiceSubject()));

        if (result.getInvoiceType().equals(InvoiceOriginEnum.QY.getValue())) {
            Customer customer = customerMapper.selectById(result.getCustomerId());
            if (Objects.nonNull(customer))
                result.setCustomerName(customer.getCustomerName());
        } else {
            CustomerContacts contacts = contactsMapper.selectById(result.getCustomerContactsId());
            if (Objects.nonNull(contacts))
                result.setCustomerName(contacts.getName());
        }
        if (StringUtils.isNotBlank(result.getOriginalInvoiceSerialNo())) {
            result.setOriginInvoiceNumber(this.baseMapper.queryInvoiceNumberBySerialNo(result.getOriginalInvoiceSerialNo()));
        }

        List<FinancialInvoiceDetailResult.LineDto> lineDtoList = financialInvoiceStocks.stream().map(a -> {
            FinancialInvoiceDetailResult.LineDto dto = FinancialInvoiceDetailResult.LineDto.
                    builder()
                    .financialInvoiceId(a.getFinancialInvoiceId())
                    .stockId(a.getStockId())
                    .forwardFiId(a.getForwardFiId())
                    .originPrice(a.getOriginPrice())
                    .originSerialNo(a.getOriginSerialNo())
                    .build();

            if (Objects.nonNull(a.getForwardFiId())) {
                FinancialInvoice invoice = this.baseMapper.selectById(a.getForwardFiId());
                if (Objects.nonNull(invoice)) {
                    dto.setForwardSerialNo(invoice.getSerialNo());
                }
            }

            WatchDataFusion fusion = watchDataFusionMap.get(dto.getStockId());
            if (Objects.nonNull(fusion)) {
                dto.setModel(fusion.getModel());
                dto.setBrandName(fusion.getBrandName());
                dto.setSeriesName(fusion.getSeriesName());
                dto.setAttachment(fusion.getAttachment());
                dto.setStockSn(fusion.getStockSn());
            }
            return dto;
        }).collect(Collectors.toList());

        result.setLines(lineDtoList);
        return result;
    }

    @Override
    public Page<FinancialInvoiceRecordPageResult> approvedMemo(FinancialInvoiceRecordRequest request) {
        return invoiceRecordMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<FinancialInvoiceQueryByConditionResult> queryByCondition(FinancialInvoiceQueryByConditionRequest request) {
        return this.baseMapper.queryByCondition(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void maycurInvoice(FinancialInvoice invoice, FinancialInvoiceStateEnum invoiceStateEnum, FinancialInvoiceStateEnum stateEnum) {
        log.info("maycurInvoice function of FinancialInvoice : {} , invoiceStateEnum : {} , stateEnum : {}", invoice, invoiceStateEnum, stateEnum);

        FinancialInvoice financialInvoice = new FinancialInvoice();
        financialInvoice.setId(invoice.getId());
        financialInvoice.setState(invoiceStateEnum);
        financialInvoice.setResult(invoice.getResult());
        financialInvoice.setBatchPictureUrl(invoice.getBatchPictureUrl());
        financialInvoice.setInvoiceTime(invoice.getInvoiceTime());
        financialInvoice.setInvoiceNumber(invoice.getInvoiceNumber());
        baseMapper.updateById(financialInvoice);

        List<FinancialInvoiceStock> invoiceStockList = invoiceStockMapper.selectList(new LambdaQueryWrapper<FinancialInvoiceStock>()
                .eq(FinancialInvoiceStock::getFinancialInvoiceId, invoice.getId()));
        switch (invoice.getOrderType()) {
            case GR_XS:
            case TH_XS:
                //将销售详情内状态改成未开票
                invoiceStockList.forEach(a -> saleOrderLineMapper.updateWhetherInvoiceById(a.getLineId(), stateEnum.getValue()));
                break;
            case GR_XS_TH:
            case TH_XS_TH:
                invoiceStockList.forEach(a -> saleReturnOrderLineMapper.updateById(BillSaleReturnOrderLine.builder().id(a.getLineId()).whetherInvoice(stateEnum).build()));
                break;
        }
    }

    @Override
    public Page<FinancialInvoiceStock> stockInfos(FinancialInvoiceStockInfosRequest request) {

        return invoiceStockMapper.selectPage(new Page(request.getPage(), request.getLimit()), new LambdaQueryWrapper<FinancialInvoiceStock>()
                .eq(FinancialInvoiceStock::getFinancialInvoiceId, request.getId()));
    }
}




