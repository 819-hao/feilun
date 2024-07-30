package com.seeease.flywheel.serve.qt.strategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.template.QtDecisionTemplate;
import com.seeease.flywheel.serve.qt.convert.QualityTestingConverter;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import com.seeease.flywheel.serve.qt.mapper.BillQualityTestingMapper;
import com.seeease.flywheel.serve.qt.service.LogQualityTestingOptService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.SeeeaseBaseException;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/4 10:39
 */

public abstract class QtDecisionStrategy implements QtDecisionTemplate<QualityTestingDecisionRequest, QualityTestingDecisionListResult> {

    @Resource
    private LogQualityTestingOptService logQualityTestingOptService;

    @Resource
    private BillQualityTestingMapper billQualityTestingMapper;

    /**
     * 前置处理
     * 1、参数转换
     * 2、参数填充
     *
     * @param request
     */
    abstract void preRequestProcessing(QualityTestingDecisionRequest request);

    /**
     * 业务校验
     * 1、必要参数校验
     * 2、金额校验
     * 3、业务可行性校验
     *
     * @param request
     * @throws BusinessException
     */
    abstract void checkRequest(QualityTestingDecisionRequest request) throws BusinessException;

    @Override
    public void preProcessing(QualityTestingDecisionRequest request) {

        this.preProcessingQt(request);

        this.preRequestProcessing(request);
    }

    /**
     * 查询质检单
     *
     * @param request
     */


    @Override
    public void check(QualityTestingDecisionRequest request) throws SeeeaseBaseException {

        Assert.notNull(request.getQualityTestingId(), "质检id不能为空");
        Assert.notNull(request.getQtState(), "质检状态不能为空");

        this.checkRequest(request);
    }

    /**
     * 更新质检状态
     *
     * @param billQualityTesting
     * @param logQualityTestingOpt
     */
    public void optAndDecisionSave(BillQualityTesting billQualityTesting, LogQualityTestingOpt logQualityTestingOpt) {

        UpdateByIdCheckState.update(billQualityTestingMapper, billQualityTesting);

        logQualityTestingOptService.save(logQualityTestingOpt);
    }

    /**
     * 判断单据来源
     *
     * @param request
     */
    private void preProcessingQt(QualityTestingDecisionRequest request) {

        BillQualityTesting billQualityTesting = billQualityTestingMapper.selectOne(Wrappers.<BillQualityTesting>lambdaQuery().eq(BillQualityTesting::getId, request.getQualityTestingId()));

        Optional.ofNullable(billQualityTesting).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.QT_BILL_NOT_EXIST));

        request.setQualityTestingDetailsResult(QualityTestingConverter.INSTANCE.convertQualityTestingDetailsResult(billQualityTesting));
    }
}
