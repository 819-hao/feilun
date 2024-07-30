package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
public interface IFinancialFacade {

    /**
     * 查询全量
     * @param request
     * @return
     */
    PageResult<FinancialPageAllResult> queryAll(FinancialQueryAllRequest request);

    /**
     * 详情
     * @param request
     * @return
     */
    List<FinancialDetailsResult> detail(FinancialDetailsRequest request);

    /**
     * 导出
     * @param request
     * @return
     */
    List<FinancialExportResult> export(FinancialQueryAllRequest request);

    /**
     * 导入金蝶
     * @param request
     * @return
     */
    String jDImport(FinancialQueryAllRequest request);

    /**
     * 财务生成工具
     * @param request
     */
    void newGenerateFinancialOrder(FinancialGenerateOrderRequest request);
}
