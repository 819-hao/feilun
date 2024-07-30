package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceStockService;
import com.seeease.flywheel.serve.financial.mapper.FinancialInvoiceStockMapper;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.springframework.stereotype.Service;

/**
 * @author edy
 * @description 针对表【financial_invoice_stock】的数据库操作Service实现
 * @createDate 2023-10-19 09:52:41
 */
@Service
public class FinancialInvoiceStockServiceImpl extends ServiceImpl<FinancialInvoiceStockMapper, FinancialInvoiceStock>
        implements FinancialInvoiceStockService {

    @Override
    public void deleteByInvoiceId(Integer id) {
        this.baseMapper.deleteByInvoiceId(id);
    }

    @Override
    public FinancialInvoiceStock getOneByStockIdAndLineId(Integer stockId, Integer lineId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<FinancialInvoiceStock>()
                .eq(FinancialInvoiceStock::getStockId, stockId)
                .eq(FinancialInvoiceStock::getLineId, lineId)
                .eq(FinancialInvoiceStock::getDeleted, WhetherEnum.NO.getValue()));
    }
}




