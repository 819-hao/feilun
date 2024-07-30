package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.mapper.PurchaseSubjectMapper;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【purchase_subject(采购主体)】的数据库操作Service实现
 * @createDate 2023-01-31 16:15:53
 */
@Service
public class PurchaseSubjectServiceImpl extends ServiceImpl<PurchaseSubjectMapper, PurchaseSubject>
        implements PurchaseSubjectService {

    @Override
    public PurchaseSubject selectPurchaseSubjectByName(String name) {
        return baseMapper.selectPurchaseSubjectByName(name);
    }

//    @Override
//    public List<PurchaseSubject> subjectCompanyQry(String name) {
//        LambdaQueryWrapper<PurchaseSubject> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.like(StringUtils.isNotEmpty(name), PurchaseSubject::getName, name);
//        List<PurchaseSubject> purchaseSubjectList = this.baseMapper.selectList(queryWrapper);
//        purchaseSubjectList = purchaseSubjectList.stream().filter(Objects::nonNull)
//                .filter(e->StringUtils.isNotEmpty(e.getSubjectCompany())).collect(Collectors.toList());
//        return purchaseSubjectList;
//    }

//    @Override
//    public List<PurchaseSubject> subjectCompanyQryBySubjectPayment(String subjectPayment) {
//        LambdaQueryWrapper<PurchaseSubject> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.like(StringUtils.isNotEmpty(subjectPayment), PurchaseSubject::getSubjectCompany, subjectPayment);
//        List<PurchaseSubject> purchaseSubjectList = this.baseMapper.selectList(queryWrapper);
//        purchaseSubjectList = purchaseSubjectList.stream().filter(Objects::nonNull)
//                .filter(e->StringUtils.isNotEmpty(e.getSubjectCompany())).collect(Collectors.toList());
//        return purchaseSubjectList;
//    }
}




