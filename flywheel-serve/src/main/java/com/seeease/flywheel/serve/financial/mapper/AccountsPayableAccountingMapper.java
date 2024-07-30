package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingQueryRequest;
import com.seeease.flywheel.financial.result.AccountsPayableAccountingPageResult;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author edy
 * @description 针对表【accounts_payable_accounting】的数据库操作Mapper
 * @createDate 2023-05-10 10:13:56
 * @Entity com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting
 */
public interface AccountsPayableAccountingMapper extends SeeeaseMapper<AccountsPayableAccounting> {

    Page<AccountsPayableAccountingPageResult> getPage(Page page, @Param("request") AccountsPayableAccountingQueryRequest request);

    void updateStatusByAfpSerialNo(@Param("serialNo") String serialNo, @Param("currStatus") Integer currStatus, @Param("toStatus") Integer toStatus);

    void updateStatusByArcSerialNo(@Param("serialNo") String serialNo, @Param("toStatus") Integer toStatus);
}




