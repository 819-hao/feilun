package com.seeease.flywheel.serve.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.customer.request.CustomerAndContactsPageQueryRequest;
import com.seeease.flywheel.customer.request.CustomerQueryRequest;
import com.seeease.flywheel.customer.result.CustomerAndContractsPageQueryResult;
import com.seeease.flywheel.customer.result.CustomerPageResult;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import org.apache.ibatis.annotations.Param;

/**
* @author dmmasxnmf
* @description 针对表【customer_contacts(供应商联系人
)】的数据库操作Mapper
* @createDate 2023-01-31 17:02:20
* @Entity com.seeease.flywheel.CustomerContacts
*/
public interface CustomerContactsMapper extends BaseMapper<CustomerContacts> {

    Page<CustomerPageResult> getPage(Page page,@Param("request") CustomerQueryRequest request);

    /**
     * 企业微信-新增确认收款单，查询客户联系人
     * @param page
     * @param request
     * @return
     */
    Page<CustomerAndContractsPageQueryResult> customerAndContractsPage(Page page, @Param("request") CustomerAndContactsPageQueryRequest request);
}




