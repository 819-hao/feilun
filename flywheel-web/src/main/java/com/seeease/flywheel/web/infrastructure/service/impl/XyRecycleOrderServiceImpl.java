package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.controller.xianyu.FlywheelXianYuClient;
import com.seeease.flywheel.web.controller.xianyu.XyRecycleConvert;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuCloseReasonCodeEnum;
import com.seeease.flywheel.web.entity.XyQtReportVO;
import com.seeease.flywheel.web.entity.XyRecycleIdleTemplate;
import com.seeease.flywheel.web.entity.XyRecycleOrder;
import com.seeease.flywheel.web.entity.XyRecycleOrderStatsVO;
import com.seeease.flywheel.web.entity.enums.XyRecycleOrderStateEnum;
import com.seeease.flywheel.web.entity.request.*;
import com.seeease.flywheel.web.infrastructure.mapper.XyRecycleIdleTemplateMapper;
import com.seeease.flywheel.web.infrastructure.mapper.XyRecycleOrderMapper;
import com.seeease.flywheel.web.infrastructure.service.XyRecycleOrderService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import com.seeease.springframework.utils.DateUtils;
import com.taobao.api.response.AlibabaIdleTemplateQuesGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Tiro
 * @description 针对表【xy_recycle_quote_order(闲鱼估价订单)】的数据库操作Service实现
 * @createDate 2023-10-20 11:35:18
 */
@Slf4j
@Service
public class XyRecycleOrderServiceImpl extends ServiceImpl<XyRecycleOrderMapper, XyRecycleOrder>
        implements XyRecycleOrderService {
    @Resource
    private XyRecycleIdleTemplateMapper templateMapper;

    @Resource
    private FlywheelXianYuClient flywheelXianYuClient;

    @Override
    public XyRecycleOrder getByQuoteId(String quoteId) {
        return baseMapper.selectOne(Wrappers.<XyRecycleOrder>lambdaQuery()
                .eq(XyRecycleOrder::getQuoteId, quoteId)
        );
    }

    @Override
    public XyRecycleOrder getByBizOrderId(String bizOrderId) {
        return baseMapper.selectOne(Wrappers.<XyRecycleOrder>lambdaQuery()
                .eq(XyRecycleOrder::getBizOrderId, bizOrderId)
        );
    }

    @Override
    public List<XyRecycleOrderStatsVO> statsByOrderState() {
        return baseMapper.statsByOrderState();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateState(XyRecycleOrder quoteOrder, XyRecycleOrderStateEnum.TransitionEnum transitionEnum) {
        quoteOrder.setTransitionStateEnum(transitionEnum);
        UpdateByIdCheckState.update(baseMapper, quoteOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(XyRecycleOrderCancelRequest request) {
        XyRecycleOrder order = baseMapper.selectById(request.getId());

        XyRecycleOrderStateEnum.TransitionEnum transitionEnum = null;
        switch (order.getQuoteOrderState()) {
            case CREATE:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.CREATE_CANCEL;
                break;
            case QUOTED:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.QUOTED_CANCEL;
                break;
            case WAIT_PICK_UP:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.WAIT_PICK_UP_CANCEL;
                break;
            default:
                throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }

        //取消闲鱼订单
        flywheelXianYuClient.orderFulfillmentCancelOrder(order.getBizOrderId(), Optional.ofNullable(request.getReason()).orElse(XianYuCloseReasonCodeEnum.OTHER));

        XyRecycleOrder upOrder = new XyRecycleOrder();
        upOrder.setId(order.getId());
        upOrder.setCloseReason(Optional.ofNullable(request.getReason()).map(XianYuCloseReasonCodeEnum::getDesc).orElse(null));
        //取消订单
        this.updateState(upOrder, transitionEnum);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quote(XyRecycleOrderQuoteRequest request) {
        if (Objects.isNull(request.getPrice()) || BigDecimalUtil.leZero(request.getPrice())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        XyRecycleOrder order = baseMapper.selectById(request.getId());

        if (!XyRecycleOrderStateEnum.CREATE.equals(order.getQuoteOrderState())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }

        //价格未改变
        if (Objects.nonNull(order.getApprizeAmount())
                && BigDecimalUtil.eq(order.getApprizeAmount(), request.getPrice())) {
            return;
        }
        XyRecycleOrderStateEnum.TransitionEnum transitionEnum = null;
        switch (order.getQuoteOrderState()) {
            case CREATE:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.QUOTED_TO_ONE;
                break;
            case QUOTED:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.QUOTED_TO;
                break;
            default:
                throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }

        //报价
        flywheelXianYuClient.recycleQuote(order.getQuoteId(), request.getPrice(), request.getSummary());

        XyRecycleOrder upOrder = new XyRecycleOrder();
        upOrder.setId(order.getId());
        upOrder.setApprizeAmount(request.getPrice());
        upOrder.setQtReport(request.getSummary());
        //报价流转
        this.updateState(upOrder, transitionEnum);
    }

    @Override
    public void pickUp(XyRecycleOrderPickUpRequest request) {
        if (StringUtils.isEmpty(request.getExpressNumber())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        XyRecycleOrder order = baseMapper.selectById(request.getId());
        if (!XyRecycleOrderStateEnum.WAIT_PICK_UP.equals(order.getQuoteOrderState())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }
        //取件
        flywheelXianYuClient.orderFulfillmentPickedUp(order.getBizOrderId(), request.getExpressNumber());
        XyRecycleOrder upOrder = new XyRecycleOrder();
        upOrder.setId(order.getId());
        upOrder.setExpressNumber(request.getExpressNumber());
        //状态流转
        this.updateState(upOrder, XyRecycleOrderStateEnum.TransitionEnum.PICK_UP);
    }

    @Override
    public void received(XyRecycleOrderReceivedRequest request) {
        if (StringUtils.isEmpty(request.getFaceImages())
                || CollectionUtils.isEmpty(request.getGoodsImages())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        XyRecycleOrder order = baseMapper.selectById(request.getId());
        if (!XyRecycleOrderStateEnum.WAIT_RECEIVED.equals(order.getQuoteOrderState())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }
        //取件
        flywheelXianYuClient.orderFulfillmentReceiving(order.getBizOrderId());
        XyRecycleOrder upOrder = new XyRecycleOrder();
        upOrder.setId(order.getId());
        upOrder.setFaceImages(request.getFaceImages());
        upOrder.setGoodsImages(request.getGoodsImages());
        //状态流转
        this.updateState(upOrder, XyRecycleOrderStateEnum.TransitionEnum.RECEIVED);
    }

    @Override
    public void qt(XyRecycleOrderQtRequest request) {
        if (Objects.isNull(request.getFinalApprizeAmount())
                || BigDecimalUtil.leZero(request.getFinalApprizeAmount())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        XyRecycleOrder order = baseMapper.selectById(request.getId());
        if (!XyRecycleOrderStateEnum.WAIT_QT.equals(order.getQuoteOrderState())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }
        //上传质检报告
//        flywheelXianYuClient.orderFulfillmentUploadReport(order.getBizOrderId(), request.getFinalApprizeAmount(), request.getQtFineness());
        //质检状态推送
        flywheelXianYuClient.orderFulfillmentQuality(order.getBizOrderId(), request.getFinalApprizeAmount(), request.getQtCode());
        XyRecycleOrder upOrder = new XyRecycleOrder();
        upOrder.setId(order.getId());
        upOrder.setModel(request.getModel());
        upOrder.setQtFineness(request.getQtFineness());
        upOrder.setQtReport(request.getQtReport());
        upOrder.setFinalApprizeAmount(request.getFinalApprizeAmount());
        upOrder.setQtCode(request.getQtCode());
        upOrder.setQtFacade(request.getQtFacade());
        upOrder.setQtDetail(request.getQtDetail());
        upOrder.setQtAttachment(request.getQtAttachment());

        //状态流转
        this.updateState(upOrder, XyRecycleOrderStateEnum.TransitionEnum.QT);
    }

    @Override
    public void payment(XyRecycleOrderPaymentRequest request) {
        if (StringUtils.isEmpty(request.getPaymentNo())
                || Objects.isNull(request.getPaymentPrice())
                || BigDecimalUtil.leZero(request.getPaymentPrice())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        XyRecycleOrder order = baseMapper.selectById(request.getId());
        if (!XyRecycleOrderStateEnum.WAIT_PAYMENT.equals(order.getQuoteOrderState())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }
        //完成打款
        flywheelXianYuClient.orderFulfillmentCompletePayment(order.getBizOrderId(), request.getPaymentPrice(), request.getPaymentNo());
        XyRecycleOrder upOrder = new XyRecycleOrder();
        upOrder.setId(order.getId());
        upOrder.setPaymentNo(request.getPaymentNo());
        upOrder.setPaymentPrice(request.getPaymentPrice());
        upOrder.setPaymentTime(Optional.ofNullable(request.getPaymentTime()).orElse(new Date()));
        //状态流转
        this.updateState(upOrder, XyRecycleOrderStateEnum.TransitionEnum.PAYMENT);
    }

    @Override
    public void refund(XyRecycleOrderRefundRequest request) {
        if (StringUtils.isEmpty(request.getRefundExpressNumber())
                || StringUtils.isEmpty(request.getRefundFaceImages())
                || CollectionUtils.isEmpty(request.getRefundGoodsImages())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }

        XyRecycleOrder order = baseMapper.selectById(request.getId());

        XyRecycleOrderStateEnum.TransitionEnum transitionEnum = null;
        switch (order.getQuoteOrderState()) {
            case WAIT_RECEIVED:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.WAIT_RECEIVED_REFUND;
                break;
            case WAIT_QT:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.WAIT_QT_REFUND;
                break;
            case QT:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.QT_REFUND;
                break;
            case APPLY_REFUND:
                transitionEnum = XyRecycleOrderStateEnum.TransitionEnum.REFUND;
                break;
            default:
                throw new OperationRejectedException(OperationExceptionCodeEnum.QUOTE_STATE_FAIL, order.getQuoteOrderState().getDesc());
        }

        //完成退回
        flywheelXianYuClient.orderFulfillmentCompleteRefund(order.getBizOrderId(), request.getRefundExpressNumber());

        XyRecycleOrder upOrder = new XyRecycleOrder();
        upOrder.setId(order.getId());
        upOrder.setRefundExpressNumber(request.getRefundExpressNumber());
        upOrder.setRefundFaceImages(request.getRefundFaceImages());
        upOrder.setRefundGoodsImages(request.getRefundGoodsImages());
        //退回订单
        this.updateState(upOrder, transitionEnum);
    }

    /**
     * 挂载问卷
     *
     * @param spuId
     * @param whole
     */
    @Override
    public void recycleSpu(String spuId, boolean whole) {
        if (StringUtils.isEmpty(spuId) && !whole) {
            return;
        }
        List<XyRecycleIdleTemplate> templateList = templateMapper.selectList(Wrappers.<XyRecycleIdleTemplate>lambdaQuery()
                .eq(StringUtils.isNoneEmpty(spuId), XyRecycleIdleTemplate::getSpuId, spuId));

        templateList.forEach(t -> {
            try {
                Long status = flywheelXianYuClient.recycleSpu(Long.valueOf(t.getSpuId()));
                XyRecycleIdleTemplate up = new XyRecycleIdleTemplate();
                up.setId(t.getId());
                up.setTemplateState(status);
                templateMapper.updateById(up);
            } catch (Exception e) {
                log.error("挂载闲鱼问卷异常:{}-{}", spuId, e.getMessage(), e);
            }
        });
    }

    @Override
    public void spuOnline(String spuId, boolean whole) {
        if (StringUtils.isEmpty(spuId) && !whole) {
            return;
        }
        List<XyRecycleIdleTemplate> templateList = templateMapper.selectList(Wrappers.<XyRecycleIdleTemplate>lambdaQuery()
                .eq(StringUtils.isNoneEmpty(spuId), XyRecycleIdleTemplate::getSpuId, spuId));

        templateList.forEach(t -> {
            try {
                //获取 最新版本
                AlibabaIdleTemplateQuesGetResponse.QuestionnaireInfoTopVO templates = flywheelXianYuClient.getTemplate(Long.valueOf(t.getSpuId()));

                //上线
                if (StringUtils.isNotEmpty(templates.getPreVersion())) {
                    flywheelXianYuClient.spuOnline(templates.getSpuId(), templates.getPreVersion());
                }

                XyRecycleIdleTemplate up = new XyRecycleIdleTemplate();
                up.setId(t.getId());
                up.setTemplateRevision(StringUtils.defaultIfEmpty(templates.getPreVersion(), templates.getOnlineVersion()));

                templateMapper.updateById(up);
            } catch (Exception e) {
                log.error("上线闲鱼问卷异常:{}-{}", spuId, e.getMessage(), e);
            }
        });
    }

    @Override
    public XyQtReportVO qtReport(String qtCode) {

        return Optional.ofNullable(baseMapper.selectOne(Wrappers.<XyRecycleOrder>lambdaQuery()
                        .eq(XyRecycleOrder::getQtCode, qtCode)))
                .map(t -> {
                    XyQtReportVO vo = XyRecycleConvert.INSTANCE.convertXyQtReportVO(t);
                    vo.setSellerImages(Stream.of(Stream.of(t.getFrontImages(), t.getBackImages(), t.getClaspImages(), t.getStrapImages())
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.toList()), t.getFlawImages())
                            .flatMap(Collection::stream)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.toList()));
                    vo.setCreatedTime(DateUtils.toDateString("yyyy.MM.dd", t.getCreatedTime()));
                    return vo;
                })
                .orElse(null);
    }

}




