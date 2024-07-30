package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;

import java.util.List;

/**
* @author dmmasxnmf
* @description 针对表【purchase_subject(采购主体)】的数据库操作Service
* @createDate 2023-01-31 16:15:53
*/
public interface PurchaseSubjectService extends IService<PurchaseSubject> {
    PurchaseSubject selectPurchaseSubjectByName(String name);

    /**
     * 查询打款主体
     * @param name
     * @return
     */
//    List<PurchaseSubject> subjectCompanyQry(String name);

    /**
     * 根据打款主体查询
     * @param subjectPayment
     * @return
     */
//    List<PurchaseSubject> subjectCompanyQryBySubjectPayment(String subjectPayment);
}
