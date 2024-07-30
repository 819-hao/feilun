package com.seeease.flywheel.serve.qt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.qt.request.FixQualityTestingRequest;
import com.seeease.flywheel.qt.request.QualityTestingCreateRequest;
import com.seeease.flywheel.qt.request.QualityTestingListRequest;
import com.seeease.flywheel.qt.request.QualityTestingWaitDeliverListRequest;
import com.seeease.flywheel.qt.result.FixQualityTestingResult;
import com.seeease.flywheel.qt.result.QualityTestingListResult;
import com.seeease.flywheel.qt.result.QualityTestingWaitDeliverListResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;

import java.util.Arrays;
import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_quality_testing】的数据库操作Service
 * @createDate 2023-01-17 11:25:43
 */
public interface BillQualityTestingService extends IService<BillQualityTesting> {

    /**
     * 创建
     *
     * @param request
     * @return
     */
    void create(List<QualityTestingCreateRequest> request);

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    Page<QualityTestingListResult> page(QualityTestingListRequest request);

    /**
     * 查询待转交列表
     *
     * @param request
     * @return
     */
    Page<QualityTestingWaitDeliverListResult> page(QualityTestingWaitDeliverListRequest request);

    /**
     * 维修质检
     *
     * @param request
     * @return
     */
    FixQualityTestingResult fix(FixQualityTestingRequest request);

    /**
     * 修改状态
     *
     * @param request
     */
    void decision(BillQualityTesting request);

    /**
     * 修改操作
     *
     * @param stockId                 商品id
     * @param deliverTo               转交方
     * @param qualityTestingStateEnum 转交状态
     */
    void update(Integer stockId, Integer deliverTo, QualityTestingStateEnum qualityTestingStateEnum);

    /**
     * 排除不能走异常
     */
    List<BusinessBillTypeEnum> ANOMALY_EXCLUDE_LIST = Arrays.asList(
    );
    /**
     * 排除不能走退货
     */
    List<BusinessBillTypeEnum> RETURN_EXCLUDE_LIST = Arrays.asList(

    );
    /**
     * 排除不能走维修
     */
    List<BusinessBillTypeEnum> FIX_EXCLUDE_LIST = Arrays.asList();


    /**
     * 是否自动定价
     *
     * @param stockId
     * @return
     */
    Boolean autoPrice(Integer stockId);

    /**
     * 转换接口
     *
     * @param resource
     * @param billQualityTesting
     * @return
     */
    default BillQualityTesting extracted(BusinessBillTypeEnum resource, BillQualityTesting billQualityTesting) {

        switch (billQualityTesting.getQtState()) {

            case NORMAL:
                billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_NORMAL_DELIVERY);
                billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.CREATE);

                //入库接口
                break;
            case ANOMALY:
                if (ANOMALY_EXCLUDE_LIST.contains(resource)) {
                    throw new OperationRejectedException(OperationExceptionCode.ANOMALY_BILL_NOT_EDIT);
                }
                billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_ANOMALY_DELIVERY);
                billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.ANOMALY);
                //入库接口
                break;
            case RETURN:

                if (RETURN_EXCLUDE_LIST.contains(resource)) {
                    throw new OperationRejectedException(OperationExceptionCode.RETURN_BILL_NOT_EDIT);
                }

                billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_RETURN_DELIVERY);
                billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.RETURN);
                //创建退货单
                break;
            case FIX:
                if (FIX_EXCLUDE_LIST.contains(resource)) {
                    throw new OperationRejectedException(OperationExceptionCode.FIX_BILL_NOT_EDIT);
                }
                billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_FIX_DELIVERY);
                billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.FIX);
                billQualityTesting.setFixFlag(1);
                break;
        }
        return billQualityTesting;
    }
}
