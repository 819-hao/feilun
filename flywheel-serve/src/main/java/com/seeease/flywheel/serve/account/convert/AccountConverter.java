package com.seeease.flywheel.serve.account.convert;

import com.seeease.flywheel.account.request.AccountCreateRequest;
import com.seeease.flywheel.account.request.AccountImportByFinanceQueryRequest;
import com.seeease.flywheel.account.request.AccountImportByManpowerQueryRequest;
import com.seeease.flywheel.account.request.AccountImportByPeopleQueryRequest;
import com.seeease.flywheel.account.result.AccountQueryResult;
import com.seeease.flywheel.serve.account.entity.KeepAccount;
import com.seeease.flywheel.serve.base.EnumConvert;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:33
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface AccountConverter extends EnumConvert {

    AccountConverter INSTANCE = Mappers.getMapper(AccountConverter.class);

    /**
     * 转换
     *
     * @param request
     * @return
     */
    AccountQueryResult convertAccountQueryResult(KeepAccount request);

    /**
     * 转换
     *
     * @param request
     * @return
     */
    KeepAccount convert(AccountCreateRequest request);

    /**
     * 导入转换
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(source = "completeDate", target = "completeDate", ignore = true),

    })
    KeepAccount convert(AccountImportByFinanceQueryRequest.ImportDto request);

    @Mappings(value = {
            @Mapping(source = "completeDate", target = "completeDate", ignore = true),

    })
    KeepAccount convert(AccountImportByManpowerQueryRequest.ImportDto request);

    @Mappings(value = {
            @Mapping(source = "completeDate", target = "completeDate", ignore = true),

    })
    KeepAccount convert(AccountImportByPeopleQueryRequest.ImportDto request);
}
