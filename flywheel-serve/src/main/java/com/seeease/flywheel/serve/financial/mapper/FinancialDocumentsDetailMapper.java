package com.seeease.flywheel.serve.financial.mapper;

import com.seeease.flywheel.serve.financial.entity.FinancialDocumentsDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author edy
 * @description 针对表【financial_documents_detail(财务单据详情)】的数据库操作Mapper
 * @createDate 2023-03-27 09:52:56
 * @Entity com.seeease.flywheel.serve.financial.entity.FinancialDocumentsDetail
 */
public interface FinancialDocumentsDetailMapper extends BaseMapper<FinancialDocumentsDetail> {

    List<FinancialDocumentsDetail> selectListBySerialNumberAndStockIds(@Param("serialNumber") String serialNo, @Param("stockIds") List<Integer> ids);
}




