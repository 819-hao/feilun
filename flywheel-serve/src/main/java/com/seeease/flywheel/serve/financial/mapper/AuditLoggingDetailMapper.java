package com.seeease.flywheel.serve.financial.mapper;

import com.seeease.flywheel.financial.request.AuditLoggingDetailRequest;
import com.seeease.flywheel.financial.result.AuditLoggingDetailResult;
import com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author edy
 * @description 针对表【audit_logging_detail】的数据库操作Mapper
 * @createDate 2023-05-10 10:14:08
 * @Entity com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail
 */
public interface AuditLoggingDetailMapper extends SeeeaseMapper<AuditLoggingDetail> {

    List<AuditLoggingDetailResult> getOneByRequest(@Param("request") AuditLoggingDetailRequest request);
}




