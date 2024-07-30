package com.seeease.flywheel.serve.financial.convert;

import com.seeease.flywheel.financial.request.AccountReceiptConfirmCreateRequest;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmCollectionDetailsResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmMiniDetailResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.AccountReceStateRel;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface AccountReceiptConfirmConvert extends EnumConvert {

    AccountReceiptConfirmConvert INSTANCE = Mappers.getMapper(AccountReceiptConfirmConvert.class);

    AccountReceiptConfirmMiniDetailResult convertAccountReceiptConfirm(AccountReceiptConfirm confirm);

    AccountReceiptConfirm convertCreateRequest(AccountReceiptConfirmCreateRequest request);

    @Mappings(value = {
            @Mapping(source = "financialStatementSerialNo", target = "fsSerialNo"),
            @Mapping(source = "createdTime", target = "confirmTime"),
            @Mapping(source = "createdBy", target = "confirmor"),
            @Mapping(source = "fundsUsed", target = "receivableAmount")
    })
    AccountReceiptConfirmCollectionDetailsResult convertReceStateRelList(AccountReceStateRel rels);
}
