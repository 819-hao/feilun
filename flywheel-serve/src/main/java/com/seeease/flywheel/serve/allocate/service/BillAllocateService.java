package com.seeease.flywheel.serve.allocate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.allocate.request.AllocateExportListRequest;
import com.seeease.flywheel.allocate.request.AllocateListRequest;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateDTO;
import com.seeease.flywheel.serve.allocate.entity.BillAllocatePO;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_allocate(调拨单)】的数据库操作Service
 * @createDate 2023-03-07 10:39:58
 */
public interface BillAllocateService extends IService<BillAllocate> {

    /**
     * 创建调拨单
     *
     * @param request
     * @return
     */
    List<BillAllocateDTO> create(AllocateCreateRequest request);

    /**
     * @param request
     * @return
     */
    Page<BillAllocate> listByRequest(AllocateListRequest request);

    Page<BillAllocate> exportListByRequest(AllocateExportListRequest request);

    /**
     * 完成品牌调拨任务
     * @param serialNo
     * @param stockIdList
     * @return
     */
    int completeBrandTaskStatus(String serialNo, List<Integer> stockIdList);

    List<BillAllocatePO> selectByStockIds(List<Integer> stockIdList);
}
