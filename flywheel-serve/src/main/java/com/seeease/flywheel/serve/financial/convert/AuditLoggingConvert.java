package com.seeease.flywheel.serve.financial.convert;


import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.AuditLogging;
import com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface AuditLoggingConvert extends EnumConvert {

    AuditLoggingConvert INSTANCE = Mappers.getMapper(AuditLoggingConvert.class);

    AuditLogging convertAccountsPayableAccounting(AccountsPayableAccounting accountsPayableAccounting);

    AuditLoggingDetail convertAuditLoggingDetail(AccountsPayableAccounting accountsPayableAccounting);
}
