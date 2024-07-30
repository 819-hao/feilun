package com.seeease.flywheel.serve.allocate.mapper;

import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_allocate_line(调拨单行)】的数据库操作Mapper
 * @createDate 2023-03-07 10:40:02
 * @Entity com.seeease.flywheel.serve.allocate.entity.BillAllocateLine
 */
public interface BillAllocateLineMapper extends SeeeaseMapper<BillAllocateLine> {

    /**
     * 查调拨单行状态
     *
     * @param allocateId
     * @return
     */
    List<Integer> selectStateByAllocateId(@Param("allocateId") Integer allocateId);
}




