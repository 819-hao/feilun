package com.seeease.flywheel.serve.purchase.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.purchase.request.PurchaseReturnListRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnListResult;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wbh
 * @date 2023/2/1
 */
public interface BillPurchaseReturnMapper extends SeeeaseMapper<BillPurchaseReturn> {

    /**
     * 分页
     *
     * @param page
     * @param request
     * @return
     */
    Page<PurchaseReturnListResult> getPage(Page page, @Param("request") PurchaseReturnListRequest request);
}
