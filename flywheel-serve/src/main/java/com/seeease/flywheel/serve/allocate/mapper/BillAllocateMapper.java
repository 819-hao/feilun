package com.seeease.flywheel.serve.allocate.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.allocate.request.AllocateExportListRequest;
import com.seeease.flywheel.allocate.request.AllocateListRequest;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocatePO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_allocate(调拨单)】的数据库操作Mapper
 * @createDate 2023-03-07 10:39:58
 * @Entity com.seeease.flywheel.serve.allocate.entity.BillAllocate
 */
public interface BillAllocateMapper extends SeeeaseMapper<BillAllocate> {

    /**
     * @param page
     * @param request
     * @return
     */
    Page<BillAllocate> listByRequest(IPage<BillAllocate> page, @Param("request") AllocateListRequest request);

    Page<BillAllocate> exportListByRequest(IPage<BillAllocate> page, @Param("request") AllocateExportListRequest request);

    /**
     * @param serialNo
     * @param stockIdList
     * @return
     */
    int completeBrandTaskStatus(@Param("serialNo") String serialNo, @Param("stockIdList") List<Integer> stockIdList);

    List<BillAllocatePO> selectByStockIds(@Param("stockIdList") List<Integer> stockIdList);
}




