package com.seeease.flywheel.serve.fix.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.fix.request.FixListRequest;
import com.seeease.flywheel.fix.result.FixListResult;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_fix(维修单)】的数据库操作Mapper
 * @createDate 2023-11-14 18:00:17
 * @Entity generator.domain.BillFix
 */
public interface BillFixMapper2 extends SeeeaseMapper<BillFix> {
    /**
     * 维修列表
     *
     * @param page
     * @param request
     * @return
     */
    Page<FixListResult> getPage(Page page, @Param("request") FixListRequest request);
}




