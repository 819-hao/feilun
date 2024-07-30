package com.seeease.flywheel.serve.purchase.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.purchase.request.PurchasePlanListRequest;
import com.seeease.flywheel.purchase.result.PurchasePlanExportResult;
import com.seeease.flywheel.purchase.result.PurchasePlanListResult;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlan;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author edy
 * @description 针对表【bill_purchase_plan】的数据库操作Mapper
 * @createDate 2023-08-08 16:58:36
 * @Entity com.seeease.flywheel.serve.purchase.entity.BillPurchasePlan
 */
public interface BillPurchasePlanMapper extends SeeeaseMapper<BillPurchasePlan> {

    Page<PurchasePlanListResult> listByRequest(Page<Object> of, @Param("request") PurchasePlanListRequest request);

    Page<PurchasePlanExportResult> export(Page<Object> of, @Param("request") PurchasePlanListRequest request);
}




