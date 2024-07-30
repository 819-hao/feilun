package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.financial.entity.FinancialTxHistory;
import com.seeease.flywheel.serve.financial.mapper.FinancialTxHistoryMapper;
import com.seeease.flywheel.serve.financial.service.FinancialTxHistoryService;
import org.springframework.stereotype.Service;

@Service
public class FinancialTxHistoryServiceImpl extends ServiceImpl<FinancialTxHistoryMapper, FinancialTxHistory>  implements FinancialTxHistoryService {
}
