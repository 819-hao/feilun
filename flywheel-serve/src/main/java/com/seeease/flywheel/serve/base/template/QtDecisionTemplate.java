package com.seeease.flywheel.serve.base.template;

import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.springframework.exception.e.SeeeaseBaseException;

/**
 * @Author Mr. Du
 * @Description 质检判定模版
 * @Date create in 2023/3/4 10:33
 */

public interface QtDecisionTemplate<T, R> {


    /**
     * 单据类型
     *
     * @return
     */
    QualityTestingStateEnum getState();

    /**
     * 前置处理
     * 1、参数转换
     * 2、参数填充
     *
     * @param t
     */
    void preProcessing(T t);

    /**
     * 业务校验
     * 1、必要参数校验
     * 2、金额校验
     * 3、业务可行性校验
     *
     * @param t
     * @throws SeeeaseBaseException
     */
    void check(T t) throws SeeeaseBaseException;

    /**
     * 创建
     * @param t
     * @return
     */
    R save(T t);

    /**
     * 创建单据
     *
     * @param t
     * @return
     */
    default R decision(T t) {
        //step1: 前置处理
        this.preProcessing(t);
        //step2: 业务校验
        this.check(t);
        //step3: 创建单据
        return this.save(t);
    }
}
