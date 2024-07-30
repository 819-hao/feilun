package com.seeease.flywheel.web.controller.express;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/1 15:57
 */

public interface ExpressStrategy<T, R> extends Serializable {


    /**
     * 获取发货类型
     *
     * @return
     */
    Integer getReceiverType();

    /**
     * 处理数据
     *
     * @param t
     * @return
     */
    default R handle(T t) {

        packageSender(t);
        packageOrder(t);
        packageReceiver(t);

        createExpressOrder(t);

        R r = execute(t);

        editExpressOrder(t);

        return r;
    }

    /**
     * 封装寄件人
     *
     * @param t
     * @return
     */
    void packageSender(T t);

    /**
     * 封装订单
     *
     * @param t
     * @return
     */
    void packageOrder(T t);

    /**
     * 封装收件人
     *
     * @param t
     * @return
     */
    void packageReceiver(T t);

    /**
     * 封装产品信息
     *
     * @param t
     * @return
     */
    void createExpressOrder(T t);

    /**
     * 执行数据
     *
     * @param t
     * @return
     */
    R execute(T t);

    /**
     * 修改快递单号
     *
     * @param t
     * @return
     */
    void editExpressOrder(T t);

}
