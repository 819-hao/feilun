package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.AuditLoggingQueryRequest;
import com.seeease.flywheel.financial.result.AuditLoggingPageResult;
import com.seeease.flywheel.serve.financial.entity.AuditLogging;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author edy
* @description 针对表【audit_logging】的数据库操作Mapper
* @createDate 2023-05-10 10:14:08
* @Entity com.seeease.flywheel.serve.financial.entity.AuditLogging
*/
public interface AuditLoggingMapper extends SeeeaseMapper<AuditLogging> {

    Page<AuditLoggingPageResult> getPage(Page page,@Param("request") AuditLoggingQueryRequest request);
}




