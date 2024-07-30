package com.seeease.flywheel.serve.customer.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.customer.request.BankQueryRequest;
import com.seeease.flywheel.customer.result.BankPageResult;
import com.seeease.flywheel.serve.customer.entity.Bank;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author edy
* @description 针对表【bank】的数据库操作Mapper
* @createDate 2023-03-01 13:47:38
* @Entity com.seeease.flywheel.serve.customer.entity.Bank
*/
public interface BankMapper extends BaseMapper<Bank> {

    Page<BankPageResult> getPage(Page page,@Param("request") BankQueryRequest request);
}




