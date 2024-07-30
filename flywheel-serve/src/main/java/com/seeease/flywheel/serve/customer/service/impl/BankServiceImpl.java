package com.seeease.flywheel.serve.customer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.customer.request.BankCreateRequest;
import com.seeease.flywheel.customer.request.BankQueryRequest;
import com.seeease.flywheel.customer.request.BankUpdateRequest;
import com.seeease.flywheel.customer.result.BankCreateResult;
import com.seeease.flywheel.customer.result.BankPageResult;
import com.seeease.flywheel.serve.customer.convert.BankConvert;
import com.seeease.flywheel.serve.customer.entity.Bank;
import com.seeease.flywheel.serve.customer.service.BankService;
import com.seeease.flywheel.serve.customer.mapper.BankMapper;
import org.springframework.stereotype.Service;

/**
 * @author edy
 * @description 针对表【bank】的数据库操作Service实现
 * @createDate 2023-03-01 13:47:38
 */
@Service
public class BankServiceImpl extends ServiceImpl<BankMapper, Bank>
        implements BankService {


    @Override
    public BankCreateResult create(BankCreateRequest request) {
        Bank bank = BankConvert.INSTANCE.convert(request);
        this.baseMapper.insert(bank);
        return BankCreateResult.builder().bankId(bank.getId()).build();
    }

    @Override
    public void update(BankUpdateRequest request) {
        Bank bank = BankConvert.INSTANCE.convert(request);
        this.baseMapper.updateById(bank);
    }

    @Override
    public Page<BankPageResult> query(BankQueryRequest request) {
        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }
}




