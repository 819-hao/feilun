package com.seeease.flywheel.serve.customer.convert;

import com.seeease.flywheel.customer.entity.CustomerContactsInfo;
import com.seeease.flywheel.customer.entity.CustomerInfo;
import com.seeease.flywheel.customer.request.CustomerCreateRequest;
import com.seeease.flywheel.customer.request.CustomerUpdateRequest;
import com.seeease.flywheel.customer.result.ContactsPageQueryResult;
import com.seeease.flywheel.customer.result.CustomerPageQueryResult;
import com.seeease.flywheel.customer.result.CustomerPageResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.customer.entity.Bank;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface CustomerConvert extends EnumConvert {

    CustomerConvert INSTANCE = Mappers.getMapper(CustomerConvert.class);

    Customer convert(CustomerCreateRequest request);

    Customer convert(CustomerUpdateRequest request);

    CustomerContacts convertContacts(CustomerUpdateRequest request);

    CustomerContacts convertContacts(CustomerCreateRequest request);

    @Mappings(value = {
            @Mapping(source = "id", target = "customerId"),
            @Mapping(source = "customerName", target = "customerName"),
            @Mapping(source = "accountName", target = "accountName"),
            @Mapping(source = "bank", target = "bank"),
            @Mapping(source = "bankAccount", target = "bankAccount"),
    })
    CustomerPageQueryResult convertCustomerPageQueryResult(Customer request);

    @Mappings(value = {
            @Mapping(source = "id", target = "contactId"),
            @Mapping(source = "name", target = "contactName"),
            @Mapping(source = "address", target = "contactAddress"),
            @Mapping(source = "phone", target = "contactPhone"),
    })
    ContactsPageQueryResult convertContactsPageQueryResult(CustomerContacts request);

    List<CustomerPageResult.Bank> convertContacts(List<Bank> selectList);

    List<CustomerInfo> convertCustomerInfo(List<Customer> customerList);

    List<CustomerContactsInfo> convertCustomerContactsInfo(List<CustomerContacts> customerContactsList);
}
