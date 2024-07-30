package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.web.entity.TmallCallbackNotify;
import com.seeease.flywheel.web.entity.tmall.TMallConsignOrderCancel;
import com.seeease.flywheel.web.entity.tmall.TMallConsignOrderNotify;
import com.seeease.flywheel.web.entity.tmall.TMallReverseOrderInStorageNotify;
import com.seeease.flywheel.web.infrastructure.service.TMallService;
import com.seeease.flywheel.web.infrastructure.service.TmallCallbackNotifyService;
import com.seeease.springframework.utils.XMLUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

/**
 * 天猫回调服务
 *
 * @author Tiro
 * @date 2023/3/24
 */
@Slf4j
@RestController
@RequestMapping("/tmall/callback")
public class TMallController {

    @Resource
    private TmallCallbackNotifyService tmallCallbackNotifyService;

    @Resource
    private TMallService tMallService;

    @PostMapping("/receive")
    public String receive(HttpServletRequest request) {
        try {
            String method = request.getParameter("method");
            TMallMethodEnum mallMethodEnum = TMallMethodEnum.forMethod(method);

            switch (mallMethodEnum) {
                case CONSIGN_ORDER_NOTIFY:
                    this.handlerOrder(request, mallMethodEnum);
                    break;

                case CONSIGN_ORDER_CANCEL:
                    this.handlerOrderCancel(request, mallMethodEnum);
                    break;

                case REVERSE_ORDER_IN_STORAGE_NOTIFY:
                    this.handlerOrderReturn(request, mallMethodEnum);
                    break;
                default:
                    log.warn("天猫接收方法为定义:{}", method);
            }

        } catch (Exception e) {
            log.error("天猫接收发生错误: {}", e.getMessage(), e);
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?><response><success>false</success><errorCode>500</errorCode><errorMsg>消息收到了,但是内部发生错误！</errorMsg><retry>false</retry></response>";
        }
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?><response><success>true</success><errorCode>200</errorCode><errorMsg>消息收到了</errorMsg><retry>false</retry></response>";
    }


    /**
     * @param request
     * @param mallMethodEnum
     * @throws IOException
     */
    private void handlerOrder(HttpServletRequest request, TMallMethodEnum mallMethodEnum) throws IOException {
        TMallConsignOrderNotify orderNotify = XMLUtil.convertStreamToObject(request.getInputStream(), TMallConsignOrderNotify.class);

        if (isHandled(mallMethodEnum, orderNotify.getBizOrderCode())) {
            return;
        }
        TmallCallbackNotify notify = new TmallCallbackNotify();
        notify.setMethod(mallMethodEnum.getMethod());
        notify.setBizOrderCode(orderNotify.getBizOrderCode());
        notify.setBody(JSONObject.toJSONString(orderNotify));
        try {
            //创建订单
            tMallService.createOrder(orderNotify);
            notify.setHandleResult(HandleResultEnum.SUCCESS.getCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            notify.setHandleResult(HandleResultEnum.FAIL.getCode());
            notify.setRemarks(e.getMessage());
        } finally {
            tmallCallbackNotifyService.save(notify);
        }

    }


    /**
     * @param request
     * @param mallMethodEnum
     * @throws IOException
     */
    private void handlerOrderCancel(HttpServletRequest request, TMallMethodEnum mallMethodEnum) throws IOException {
        TMallConsignOrderCancel consignOrderCancel = XMLUtil.convertStreamToObject(request.getInputStream(), TMallConsignOrderCancel.class);

        if (isHandled(mallMethodEnum, consignOrderCancel.getBizOrderCode())) {
            return;
        }
        TmallCallbackNotify notify = new TmallCallbackNotify();
        notify.setMethod(mallMethodEnum.getMethod());
        notify.setBizOrderCode(consignOrderCancel.getBizOrderCode());
        notify.setBody(JSONObject.toJSONString(consignOrderCancel));
        try {
            //取消订单
            tMallService.cancelOrder(consignOrderCancel);
            notify.setHandleResult(HandleResultEnum.SUCCESS.getCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            notify.setHandleResult(HandleResultEnum.FAIL.getCode());
            notify.setRemarks(e.getMessage());
        } finally {
            tmallCallbackNotifyService.save(notify);
        }

    }


    /**
     * @param request
     * @param mallMethodEnum
     * @throws IOException
     */
    private void handlerOrderReturn(HttpServletRequest request, TMallMethodEnum mallMethodEnum) throws IOException {
        TMallReverseOrderInStorageNotify inStorageNotify = XMLUtil.convertStreamToObject(request.getInputStream(), TMallReverseOrderInStorageNotify.class);

        if (isHandled(mallMethodEnum, inStorageNotify.getBizOrderCode())) {
            return;
        }
        TmallCallbackNotify notify = new TmallCallbackNotify();
        notify.setMethod(mallMethodEnum.getMethod());
        notify.setBizOrderCode(inStorageNotify.getBizOrderCode());
        notify.setBody(JSONObject.toJSONString(inStorageNotify));
        try {
            //退货订单
            tMallService.reverseOrder(inStorageNotify);
            notify.setHandleResult(HandleResultEnum.SUCCESS.getCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            notify.setHandleResult(HandleResultEnum.FAIL.getCode());
            notify.setRemarks(e.getMessage());
        } finally {
            tmallCallbackNotifyService.save(notify);
        }

    }

    /**
     * 已处理
     *
     * @param mallMethodEnum
     * @param bizOrderCode
     * @return
     */
    boolean isHandled(TMallMethodEnum mallMethodEnum, String bizOrderCode) {
        return tmallCallbackNotifyService.count(Wrappers.<TmallCallbackNotify>lambdaQuery()
                .eq(TmallCallbackNotify::getMethod, mallMethodEnum.getMethod())
                .eq(TmallCallbackNotify::getBizOrderCode, bizOrderCode)) > 0;
    }


    @Getter
    @AllArgsConstructor
    public enum TMallMethodEnum {

        UN_DEFINITION("未定义"),
        /**
         * 天猫订单推送
         */
        CONSIGN_ORDER_NOTIFY("qimen.alibaba.ascp.uop.consignorder.notify"),
        /**
         * 天猫订单取消推送
         */
        CONSIGN_ORDER_CANCEL("qimen.alibaba.ascp.uop.consignorder.cancel"),
        /**
         * 天猫销退单推送
         */
        REVERSE_ORDER_IN_STORAGE_NOTIFY("qimen.alibaba.ascp.uop.reverseorder.instorage.notify");

        private String method;

        public static TMallMethodEnum forMethod(String method) {
            return Arrays.stream(TMallMethodEnum.values())
                    .filter(t -> t.getMethod().equals(method))
                    .findFirst()
                    .orElse(UN_DEFINITION);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum HandleResultEnum {
        FAIL(-1, "异常"),
        SUCCESS(1, "成功");
        private int code;
        private String method;
    }
}
