package com.seeease.flywheel.serve.financial.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmDetailRequest;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmDetailResult;
import com.seeease.flywheel.serve.financial.entity.AccountReceStateRel;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author edy
 * @description 针对表【account_rece_state_rel(应收记录与流水关系表)】的数据库操作Mapper
 * @createDate 2023-09-11 15:17:04
 * @Entity com.seeease.flywheel.serve.financial.entity.AccountReceStateRel
 */
public interface AccountReceStateRelMapper extends SeeeaseMapper<AccountReceStateRel> {

    Page<AccountReceiptConfirmDetailResult> getDetailPage(Page page, @Param("request") AccountReceiptConfirmDetailRequest request);
}




