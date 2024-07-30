package com.seeease.flywheel.web.controller.xianyu.strategy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMethodEnum;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuShipTypeEnum;
import com.seeease.flywheel.web.controller.xianyu.request.RecycleQuoteGetRequest;
import com.seeease.flywheel.web.controller.xianyu.result.RecycleQuoteGetResult;
import com.seeease.flywheel.web.entity.XyRecycleIdleTemplate;
import com.seeease.flywheel.web.entity.XyRecycleOrder;
import com.seeease.flywheel.web.entity.enums.XyRecycleOrderStateEnum;
import com.seeease.flywheel.web.infrastructure.service.XyRecycleIdleTemplateService;
import com.seeease.flywheel.web.infrastructure.service.XyRecycleOrderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 问卷估价处理
 *
 * @author Tiro
 * @date 2023/10/13
 */
@Slf4j
@Component
public class RecycleQuoteGetRequestProcessor implements BaseQiMenRequestProcessor<RecycleQuoteGetRequest, RecycleQuoteGetResult> {
    @Resource
    private XyRecycleOrderService quoteOrderService;
    @Resource
    private XyRecycleIdleTemplateService templateService;

    @Override
    public Class<RecycleQuoteGetRequest> requestClass() {
        return RecycleQuoteGetRequest.class;
    }

    @Override
    public XianYuMethodEnum getMethodEnum() {
        return XianYuMethodEnum.RECYCLE_QUOTE_GET;
    }

    @Override
    public RecycleQuoteGetResult execute(RecycleQuoteGetRequest request) {
        log.info("闲鱼估价数据:{}", JSONObject.toJSONString(request));

        XyRecycleOrder record = new XyRecycleOrder();
        record.setQuoteId(request.getQuoteId());
        record.setQuoteOrderState(XyRecycleOrderStateEnum.CREATE);
        record.setSpuId(request.getSpuId());
        record.setBrandName(Optional.ofNullable(templateService.getOneBySpuId(request.getSpuId())).map(XyRecycleIdleTemplate::getBrandName).orElse(null));
        record.setBizType(request.getBizType());
        record.setVersion(request.getVersion());
        record.setChannel(request.getChannel());
        record.setUserId(request.getUserId());
        record.setQuestionnaire(request.getQuestionnaire());
        try {
            Map<String, QuestionnaireElement> qeMap = JSONArray.parseArray(request.getQuestionnaire(), QuestionnaireElement.class)
                    .stream()
                    .collect(Collectors.toMap(QuestionnaireElement::getName, Function.identity(), (k1, k2) -> k2));

            //图片
            QuestionnaireElement imageElement = qeMap.get("上传宝贝图片");
            if (Objects.nonNull(imageElement) && CollectionUtils.isNotEmpty(imageElement.getAnswers())) {
                Map<String, List<Answers>> answersMap = imageElement.getAnswers()
                        .stream()
                        .collect(Collectors.groupingBy(Answers::getName));

                record.setFrontImages(Optional.ofNullable(answersMap.get("正面整体图"))
                        .map(t -> t.stream()
                                .map(Answers::getValueList)
                                .filter(CollectionUtils::isNotEmpty)
                                .flatMap(Collection::stream)
                                .map(ValueList::getUrl)
                                .findFirst()
                                .orElse(null))
                        .orElse(null));

                record.setBackImages(Optional.ofNullable(answersMap.get("表盘背面图"))
                        .map(t -> t.stream()
                                .map(Answers::getValueList)
                                .filter(CollectionUtils::isNotEmpty)
                                .flatMap(Collection::stream)
                                .map(ValueList::getUrl)
                                .findFirst()
                                .orElse(null))
                        .orElse(null));

                record.setClaspImages(Optional.ofNullable(answersMap.get("表扣细节图"))
                        .map(t -> t.stream()
                                .map(Answers::getValueList)
                                .filter(CollectionUtils::isNotEmpty)
                                .flatMap(Collection::stream)
                                .map(ValueList::getUrl)
                                .findFirst()
                                .orElse(null))
                        .orElse(null));

                record.setStrapImages(Optional.ofNullable(answersMap.get("表带细节图"))
                        .map(t -> t.stream()
                                .map(Answers::getValueList)
                                .filter(CollectionUtils::isNotEmpty)
                                .flatMap(Collection::stream)
                                .map(ValueList::getUrl)
                                .findFirst()
                                .orElse(null))
                        .orElse(null));

                record.setFlawImages(Optional.ofNullable(answersMap.get("瑕疵图"))
                        .map(t -> t.stream()
                                .map(Answers::getValueList)
                                .filter(CollectionUtils::isNotEmpty)
                                .flatMap(Collection::stream)
                                .map(ValueList::getUrl)
                                .collect(Collectors.toList()))
                        .orElse(Collections.emptyList()));
            }


            //表镜尺寸
            QuestionnaireElement watchSizeElement = qeMap.get("表镜尺寸");
            if (Objects.nonNull(watchSizeElement) && CollectionUtils.isNotEmpty(watchSizeElement.getAnswers())) {
                record.setWatchSize(watchSizeElement.getAnswers().stream()
                        .map(Answers::getName)
                        .findFirst()
                        .orElse(null));
            }


            //机芯类型
            QuestionnaireElement movementElement = qeMap.get("机芯类型");
            if (Objects.nonNull(movementElement) && CollectionUtils.isNotEmpty(movementElement.getAnswers())) {
                record.setMovementType(movementElement.getAnswers().stream()
                        .map(Answers::getName)
                        .findFirst()
                        .orElse(null));
            }

            //配件（可多选）
            QuestionnaireElement attachmentElement = qeMap.get("配件（可多选）");
            if (Objects.nonNull(attachmentElement) && CollectionUtils.isNotEmpty(attachmentElement.getAnswers())) {
                record.setAttachment(attachmentElement.getAnswers().stream()
                        .map(Answers::getName)
                        .collect(Collectors.joining("/")));
            }

            //使用情况
            QuestionnaireElement usageStatusElement = qeMap.get("使用情况");
            if (Objects.nonNull(usageStatusElement) && CollectionUtils.isNotEmpty(usageStatusElement.getAnswers())) {
                record.setUsageStatus(usageStatusElement.getAnswers().stream()
                        .map(Answers::getName)
                        .findFirst()
                        .orElse(null));
            }
        } catch (Exception e) {
            log.error("闲鱼估价问卷数据解析异常,{}", e.getMessage(), e);
        }

        //估价记录
        quoteOrderService.save(record);

        RecycleQuoteGetResult result = RecycleQuoteGetResult.builder()
                .spuId(request.getSpuId())
                .quoteId(request.getQuoteId())
                .price(1L)
                .shipTypes(Lists.newArrayList(XianYuShipTypeEnum.SF.getValue())) //上门
                .build();

        return result;
    }


    @Data
    public static class QuestionnaireElement implements Serializable {
        private Long id;
        private String name;
        private String questionType;
        private String remark;
        private Boolean required;
        private List<Answers> answers;
    }

    @Data
    public static class Answers implements Serializable {
        private Long id;
        private String name;
        private List<ValueList> valueList;
    }

    @Data
    public static class ValueList implements Serializable {
        private String url;
        private Long height;
        private Long width;
    }

}
