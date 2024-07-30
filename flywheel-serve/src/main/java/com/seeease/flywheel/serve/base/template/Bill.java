package com.seeease.flywheel.serve.base.template;

import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.springframework.exception.e.SeeeaseBaseException;

/**
 * @author Tiro
 * @date 2023/3/2
 */
public interface Bill<T, R> {

    /**
     * 单据类型
     *
     * @return
     */
    BusinessBillTypeEnum getType();

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
    void bizCheck(T t) throws SeeeaseBaseException;

    /**
     * 创建单据
     *
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
    default R crete(T t) {
        //step1: 前置处理
        this.preProcessing(t);
        //step2: 业务校验
        this.bizCheck(t);
        //step3: 创建单据
        return this.save(t);
    }
}
