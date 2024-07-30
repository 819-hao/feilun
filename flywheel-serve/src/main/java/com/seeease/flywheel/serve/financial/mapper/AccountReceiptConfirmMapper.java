package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmMiniPageRequest;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmPageRequest;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmMiniPageResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmPageResult;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wuyu
 * @description 针对表【account_receipt_confirm(确认收款表)】的数据库操作Mapper
 * @createDate 2023-09-12 11:12:35
 * @Entity generator.domain.AccountReceiptConfirm
 */
public interface AccountReceiptConfirmMapper extends SeeeaseMapper<AccountReceiptConfirm> {


    Page<AccountReceiptConfirmMiniPageResult> getMiniPage(Page page, @Param("request") AccountReceiptConfirmMiniPageRequest request);


    Page<AccountReceiptConfirmPageResult> getPCPage(Page page, @Param("request") AccountReceiptConfirmPageRequest request);


}




