package com.seeease.flywheel.serve.purchase.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.purchase.request.PurchaseDemandPageRequest;
import com.seeease.flywheel.purchase.result.PurchaseDemandPageResult;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseDemand;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlanLine;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;


public interface BillPurchaseDemandMapper extends SeeeaseMapper<BillPurchaseDemand> {

    Page<PurchaseDemandPageResult> page(Page page,@Param("req") PurchaseDemandPageRequest request);
}




