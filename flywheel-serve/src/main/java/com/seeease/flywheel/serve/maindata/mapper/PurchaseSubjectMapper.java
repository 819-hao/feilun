package com.seeease.flywheel.serve.maindata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import org.apache.ibatis.annotations.Param;

/**
* @author dmmasxnmf
* @description 针对表【purchase_subject(采购主体)】的数据库操作Mapper
* @createDate 2023-01-31 16:15:53
* @Entity com.seeease.flywheel.PurchaseSubject
*/
public interface PurchaseSubjectMapper extends BaseMapper<PurchaseSubject> {

    String selectNameById(@Param("subjectPayment") Integer subjectPayment);

    PurchaseSubject selectPurchaseSubjectByName(@Param("company") String company);
}




