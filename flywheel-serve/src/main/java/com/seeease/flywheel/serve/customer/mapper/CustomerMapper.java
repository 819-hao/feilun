package com.seeease.flywheel.serve.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【customer(供应商表)】的数据库操作Mapper
 * @createDate 2023-01-31 17:02:14
 * @Entity com.seeease.flywheel.Customer
 */
public interface CustomerMapper extends BaseMapper<Customer> {

    CustomerPO queryCustomerPO(@Param("customerContactsId") Integer customerContactsId);

    List<Customer> searchByNameOrPhone(@Param("customerName") String customerName, @Param("customerPhone") String customerPhone);

    Customer queryByName(@Param("name") String name);
}




