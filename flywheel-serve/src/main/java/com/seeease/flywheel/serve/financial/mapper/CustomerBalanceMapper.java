package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.customer.request.CustomerBalancePageRequest;
import com.seeease.flywheel.customer.result.CustomerBalancePageResult;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmDetailRequest;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmDetailResult;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author wuyu
* @description 针对表【customer_balance(客户余额
)】的数据库操作Mapper
* @createDate 2023-09-12 11:12:35
* @Entity generator.domain.CustomerBalance
*/
public interface CustomerBalanceMapper extends SeeeaseMapper<CustomerBalance> {

    Page<CustomerBalancePageResult> getCustomerBalancePage(Page page, @Param("request") CustomerBalancePageRequest request);

}




