package com.seeease.flywheel.web.controller.xianyu;

import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuCloseReasonCodeEnum;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuOrderStatusEnum;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuShipTypeEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import com.seeease.springframework.utils.DateUtils;
import com.taobao.api.*;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

/**
 * TOP商家请求奇门接口
 *
 * @author Tiro
 * @date 2023/10/16
 */
@Slf4j
public class FlywheelXianYuClient implements TaobaoClient {
    private String serverUrl;
    private String appKey;
    private String appSecret;
    private String sessionKey;
    private TaobaoClient client;

    public FlywheelXianYuClient(String serverUrl, String appKey, String appSecret, String sessionKey) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.sessionKey = sessionKey;
        this.client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
    }


    /**
     * 获取闲鱼问卷最新版本
     *
     * @param spuId
     * @return
     */
    public AlibabaIdleTemplateQuesGetResponse.QuestionnaireInfoTopVO getTemplate(Long spuId) {
        try {
            AlibabaIdleTemplateQuesGetRequest req = new AlibabaIdleTemplateQuesGetRequest();
            AlibabaIdleTemplateQuesGetRequest.SpuQuestionnaireTopQry obj1 = new AlibabaIdleTemplateQuesGetRequest.SpuQuestionnaireTopQry();
            obj1.setBizType("IDLE_RECYCLE_LUXURIES");
            obj1.setSpuId(spuId);
            req.setSpuQuestionnaireTopQry(obj1);
            AlibabaIdleTemplateQuesGetResponse rsp = client.execute(req, this.sessionKey);
            log.info("闲鱼问卷查询请求成功:{},result={}", spuId, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return rsp.getResult().getData();
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼问卷查询异常:{}", e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    /**
     * 闲鱼问卷上线
     *
     * @param spuId
     * @param preVersion
     */
    public void spuOnline(Long spuId, String preVersion) {
        try {
            AlibabaIdleTemplateQuesOnlineRequest req = new AlibabaIdleTemplateQuesOnlineRequest();
            AlibabaIdleTemplateQuesOnlineRequest.QuestionnaireSupportCmd obj1 = new AlibabaIdleTemplateQuesOnlineRequest.QuestionnaireSupportCmd();
            obj1.setBizType("IDLE_RECYCLE_LUXURIES");
            obj1.setSpuId(spuId);
            obj1.setPreVersion(preVersion);
            req.setQuestionnaireSupportCmd(obj1);
            AlibabaIdleTemplateQuesOnlineResponse rsp = client.execute(req, sessionKey);
            log.info("闲鱼问卷上线请求成功:{},result={}", spuId, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼问卷上线异常:{}", e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }


    /**
     * 挂载问卷SPU, 返回当前闲鱼问卷状态，0-在线，1-测试
     *
     * @param spuId
     * @return
     */
    public Long recycleSpu(Long spuId) {
        try {
            AlibabaIdleRecycleSpuTemplateModifyRequest req = new AlibabaIdleRecycleSpuTemplateModifyRequest();
            AlibabaIdleRecycleSpuTemplateModifyRequest.RecycleSpuTemplate obj1 = new AlibabaIdleRecycleSpuTemplateModifyRequest.RecycleSpuTemplate();
            obj1.setActionType(0L);
            //obj1.setRecycleSupplierId(1201L);
            obj1.setBizCode("LUXURIES");
            obj1.setSpuId(spuId);
            obj1.setRecycleType(1L);
            obj1.setPdCode("HS");
            req.setRecycleSpuTemplate(obj1);
            AlibabaIdleRecycleSpuTemplateModifyResponse rsp = client.execute(req);
            log.info("闲鱼问卷挂载请求成功:{},result={}", spuId, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return rsp.getResult().getSpuStatus();
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL,
                    Optional.of(rsp.getResult())
                            .map(AlibabaIdleRecycleSpuTemplateModifyResponse.RecycleResult::getErrMessage)
                            .orElse(rsp.getSubMessage()));
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼问卷挂载异常:{}", e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    /**
     * 问卷报价
     *
     * @param quoteId
     * @param price
     * @param summary
     */
    public void recycleQuote(String quoteId, BigDecimal price, String summary) {
        try {
            AlibabaIdleRecycleInspectionReportRequest req = new AlibabaIdleRecycleInspectionReportRequest();
            AlibabaIdleRecycleInspectionReportRequest.InspectionReport obj1 = new AlibabaIdleRecycleInspectionReportRequest.InspectionReport();
            obj1.setPrice(BigDecimalUtil.yuanToCent(price));
            obj1.setSuccess(true);
            obj1.setQuoteId(quoteId);
            obj1.setSummary(summary);
            obj1.setReport("{\"questions\":[]}");
            AlibabaIdleRecycleInspectionReportRequest.HashMap obj2 = new AlibabaIdleRecycleInspectionReportRequest.HashMap();
            obj2.setShipType(Arrays.asList(XianYuShipTypeEnum.SF.getValue()));
            obj1.setExtAttributes(obj2);
            req.setInspectionReport(obj1);
            AlibabaIdleRecycleInspectionReportResponse rsp = client.execute(req);
            log.info("闲鱼报价请求成功:{}={},result={}", quoteId, price, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL,
                    Optional.of(rsp.getResult())
                            .map(AlibabaIdleRecycleInspectionReportResponse.RecycleResult::getErrMsg)
                            .orElse(rsp.getSubMessage()));
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼报价异常:{}", e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    /**
     * 查询闲鱼回收订单
     *
     * @param bizOrderId
     * @return
     */
    public AlibabaIdleRecycleOrderQueryResponse queryOrder(Long bizOrderId) {
        try {
            AlibabaIdleRecycleOrderQueryRequest req = new AlibabaIdleRecycleOrderQueryRequest();
            req.setBizOrderId(bizOrderId);
            AlibabaIdleRecycleOrderQueryResponse rsp = client.execute(req);
            log.info("闲鱼订单查询成功:{},result={}", bizOrderId, rsp.getBody());
            return rsp;
        } catch (Exception e) {
            log.error("闲鱼订单查询异常:{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 订单履约-回应修改地址请求
     *
     * @param bizOrderId
     * @param agreeUseAddressChange
     */
    public void orderFulfillmentChangeAddress(String bizOrderId, boolean agreeUseAddressChange) {
        try {
            AlibabaIdleRecycleOrderFulfillmentRequest req = new AlibabaIdleRecycleOrderFulfillmentRequest();
            AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto obj1 = new AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto();
            AlibabaIdleRecycleOrderFulfillmentRequest.Attribute obj2 = new AlibabaIdleRecycleOrderFulfillmentRequest.Attribute();
            obj1.setBizOrderId(bizOrderId);
            obj1.setOrderStatus(XianYuOrderStatusEnum.CREATE_DONE.getCode() + "");
            obj1.setPartnerKey(this.appKey);
            obj2.setAgreeUseAddressChange(String.valueOf(agreeUseAddressChange));
            obj1.setAttribute(obj2);
            req.setParam0(obj1);
            AlibabaIdleRecycleOrderFulfillmentResponse rsp = client.execute(req);
            log.info("闲鱼订单回应修改地址请求成功:params[{}:{}],result={}", bizOrderId, agreeUseAddressChange, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单回应修改地址异常:params[{}:{}],e:{}", bizOrderId, agreeUseAddressChange, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    /**
     * 订单履约-取件
     *
     * @param bizOrderId
     * @param expressNumber
     */
    public void orderFulfillmentPickedUp(String bizOrderId, String expressNumber) {
        try {
            AlibabaIdleRecycleOrderFulfillmentRequest req = new AlibabaIdleRecycleOrderFulfillmentRequest();
            AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto obj1 = new AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto();
            AlibabaIdleRecycleOrderFulfillmentRequest.Attribute obj2 = new AlibabaIdleRecycleOrderFulfillmentRequest.Attribute();
            obj1.setBizOrderId(bizOrderId);
            obj1.setOrderStatus(XianYuOrderStatusEnum.BUYER_PACKED.getCode() + "");
            obj1.setPartnerKey(this.appKey);
            obj2.setMailNo(expressNumber);
            obj1.setAttribute(obj2);
            req.setParam0(obj1);
            AlibabaIdleRecycleOrderFulfillmentResponse rsp = client.execute(req);
            log.info("闲鱼订单履约取件请求成功:params[{}:{}],result={}", bizOrderId, expressNumber, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单履约取件异常:params[{}:{}],e:{}", bizOrderId, expressNumber, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }


    /**
     * 订单履约-已收货
     *
     * @param bizOrderId
     */
    public void orderFulfillmentReceiving(String bizOrderId) {
        try {
            AlibabaIdleRecycleOrderFulfillmentRequest req = new AlibabaIdleRecycleOrderFulfillmentRequest();
            AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto obj1 = new AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto();
            AlibabaIdleRecycleOrderFulfillmentRequest.Attribute obj2 = new AlibabaIdleRecycleOrderFulfillmentRequest.Attribute();
            obj1.setBizOrderId(bizOrderId);
            obj1.setOrderStatus(XianYuOrderStatusEnum.BUYER_RECEIVING.getCode() + "");
            obj1.setPartnerKey(this.appKey);
            obj2.setIsvReceiveTime(DateUtils.toDateString(DateUtils.YMD_HMS, new Date()));
            obj1.setAttribute(obj2);
            req.setParam0(obj1);
            AlibabaIdleRecycleOrderFulfillmentResponse rsp = client.execute(req);
            log.info("闲鱼订单履约收货请求成功:params[{}],result={}", bizOrderId, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单履约收货异常:params[{}],e:{}", bizOrderId, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    /**
     * 订单履约-上传质检报告
     *
     * @param bizOrderId
     * @param price
     */
    public void orderFulfillmentUploadReport(String bizOrderId, BigDecimal price, String qtFineness) {
        try {
            AlibabaIdleRecycleInspectionReportRequest req = new AlibabaIdleRecycleInspectionReportRequest();
            AlibabaIdleRecycleInspectionReportRequest.InspectionReport obj1 = new AlibabaIdleRecycleInspectionReportRequest.InspectionReport();
            obj1.setPrice(BigDecimalUtil.yuanToCent(price));
            obj1.setSuccess(true);
            obj1.setOrderId(Long.valueOf(bizOrderId));

            obj1.setDegree(qtFineness);
//            obj1.setSummary("外观良好，表单需要更换");
            obj1.setReport("{\"questions\":[]}");
//            obj1.setExplanation("{\"desc\":\"检测报告\",\"images\":[\"https:\\/\\/img.alicdn.com\\/imgextra\\/i2\\/2207288991077\\/O1CN01Jgb4v61JpJGjZBxsE_!!2207288991077.png\"]}");
            req.setInspectionReport(obj1);
            AlibabaIdleRecycleInspectionReportResponse rsp = client.execute(req);
            log.info("闲鱼订单履约上传质检报告请求成功:params[{}:{}:{}],result={}", bizOrderId, price, qtFineness, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL,
                    Optional.of(rsp.getResult())
                            .map(AlibabaIdleRecycleInspectionReportResponse.RecycleResult::getErrMsg)
                            .orElse(rsp.getSubMessage()));
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单履约上传质检报告异常:params[{}:{}:{}],e:{}", bizOrderId, price, qtFineness, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }


    /**
     * 订单履约-质检
     *
     * @param bizOrderId
     * @param price
     * @param qtCode
     */
    public void orderFulfillmentQuality(String bizOrderId, BigDecimal price
            , String qtCode) {
        try {
            AlibabaIdleRecycleOrderFulfillmentRequest req = new AlibabaIdleRecycleOrderFulfillmentRequest();
            AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto obj1 = new AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto();
            AlibabaIdleRecycleOrderFulfillmentRequest.Attribute obj2 = new AlibabaIdleRecycleOrderFulfillmentRequest.Attribute();

            obj1.setBizOrderId(bizOrderId);
            obj1.setOrderStatus(XianYuOrderStatusEnum.BUYER_QUALITY_CHECKED.getCode() + "");
            obj1.setPartnerKey(this.appKey);

            obj2.setConfirmFee(BigDecimalUtil.yuanToCent(price) + "");
            obj2.setReportUrl("https://feilun.seeease.com/xy/index.html?qtCode=" + qtCode);
            obj1.setAttribute(obj2);

            req.setParam0(obj1);
            AlibabaIdleRecycleOrderFulfillmentResponse rsp = client.execute(req);
            log.info("闲鱼订单履约质检请求成功:params[{}:{}],result={}", bizOrderId, price, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单履约质检异常:params[{}:{}],e:{}", bizOrderId, price, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    /**
     * 订单履约-完成打款
     *
     * @param bizOrderId
     * @param price
     */
    public void orderFulfillmentCompletePayment(String bizOrderId, BigDecimal price, String alipayTradeNo) {
        try {
            AlibabaIdleRecycleOrderFulfillmentRequest req = new AlibabaIdleRecycleOrderFulfillmentRequest();
            AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto obj1 = new AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto();
            AlibabaIdleRecycleOrderFulfillmentRequest.Attribute obj2 = new AlibabaIdleRecycleOrderFulfillmentRequest.Attribute();
            obj1.setBizOrderId(bizOrderId);
            obj1.setOrderStatus(XianYuOrderStatusEnum.BUYER_ORDER_CONFIRMED.getCode() + "");
            obj1.setPartnerKey(this.appKey);

            obj2.setConfirmFee(BigDecimalUtil.yuanToCent(price) + "");
            obj2.setAlipayTradeNo(alipayTradeNo);
            obj1.setAttribute(obj2);
            req.setParam0(obj1);
            AlibabaIdleRecycleOrderFulfillmentResponse rsp = client.execute(req);
            log.info("闲鱼订单履约完成打款请求成功:params[{}:{}:{}],result={}", bizOrderId, price, alipayTradeNo, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单履约完成打款异常:params[{}:{}:{}],e:{}", bizOrderId, price, alipayTradeNo, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }


    /**
     * 订单履约-商家取消订单
     *
     * @param bizOrderId
     * @param reasonCodeEnum
     */
    public void orderFulfillmentCancelOrder(String bizOrderId, XianYuCloseReasonCodeEnum reasonCodeEnum) {
        try {
            AlibabaIdleRecycleOrderFulfillmentRequest req = new AlibabaIdleRecycleOrderFulfillmentRequest();
            AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto obj1 = new AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto();
            AlibabaIdleRecycleOrderFulfillmentRequest.Attribute obj2 = new AlibabaIdleRecycleOrderFulfillmentRequest.Attribute();
            obj1.setBizOrderId(bizOrderId);
            obj1.setOrderStatus(XianYuOrderStatusEnum.BUYER_CANCLE_ORDER.getCode() + "");
            obj1.setPartnerKey(this.appKey);
            obj2.setCloseReasonCode(reasonCodeEnum.getCode());
            obj2.setReason(reasonCodeEnum.getDesc());
            obj1.setAttribute(obj2);
            req.setParam0(obj1);
            AlibabaIdleRecycleOrderFulfillmentResponse rsp = client.execute(req);
            log.info("闲鱼订单履约商家取消订单请求成功:params[{}:{}],result={}", bizOrderId, reasonCodeEnum, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单履约商家取消订单异常:params[{}:{}],e:{}", bizOrderId, reasonCodeEnum, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    /**
     * 订单履约-商家完成退货
     *
     * @param bizOrderId
     * @param refundExpressNumber
     */
    public void orderFulfillmentCompleteRefund(String bizOrderId, String refundExpressNumber) {
        try {
            AlibabaIdleRecycleOrderFulfillmentRequest req = new AlibabaIdleRecycleOrderFulfillmentRequest();
            AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto obj1 = new AlibabaIdleRecycleOrderFulfillmentRequest.RecycleOrderSynDto();
            AlibabaIdleRecycleOrderFulfillmentRequest.Attribute obj2 = new AlibabaIdleRecycleOrderFulfillmentRequest.Attribute();
            obj1.setBizOrderId(bizOrderId);
            obj1.setOrderStatus(XianYuOrderStatusEnum.GOOD_HAS_REDUND.getCode() + "");
            obj1.setPartnerKey(this.appKey);

            obj2.setMailNo(refundExpressNumber);
            obj1.setAttribute(obj2);

            req.setParam0(obj1);
            AlibabaIdleRecycleOrderFulfillmentResponse rsp = client.execute(req);
            log.info("闲鱼订单履约完成退货请求成功:params[{}:{}],result={}", bizOrderId, refundExpressNumber, rsp.getBody());
            if (rsp.isSuccess() && rsp.getResult().getSuccess()) {
                return;
            }
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, rsp.getSubMessage());
        } catch (OperationRejectedException e) {
            throw e;
        } catch (Exception e) {
            log.error("闲鱼订单履约完成退货异常:params[{}:{}],e:{}", bizOrderId, refundExpressNumber, e.getMessage(), e);
            throw new OperationRejectedException(OperationExceptionCodeEnum.XIAN_YU_API_FAIL, "请求异常！");
        }
    }

    @Override
    public <T extends TaobaoResponse> T execute(TaobaoRequest<T> request) throws ApiException {
        return client.execute(request);
    }

    @Override
    public <T extends TaobaoResponse> T execute(TaobaoRequest<T> request, String s) throws ApiException {
        return client.execute(request, s);
    }
}
