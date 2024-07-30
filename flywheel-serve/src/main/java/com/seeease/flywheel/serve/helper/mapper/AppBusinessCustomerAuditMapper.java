package com.seeease.flywheel.serve.helper.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.helper.request.BusinessCustomerListRequest;
import com.seeease.flywheel.helper.result.BusinessCustomerPageResult;
import com.seeease.flywheel.serve.helper.entity.BusinessCustomerAudit;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

public interface AppBusinessCustomerAuditMapper extends SeeeaseMapper<BusinessCustomerAudit> {

    Page<BusinessCustomerPageResult> pageOf(Page<Object> of,
                                            @Param("req") BusinessCustomerListRequest request,
                                            @Param("admin") boolean admin,
                                            @Param("userid") Integer userid);
}
