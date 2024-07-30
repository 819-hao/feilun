package com.seeease.flywheel.web.controller.xianyu.strategy;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMethodEnum;
import com.seeease.flywheel.web.controller.xianyu.request.RecycleQuoteTemplateRequest;
import com.seeease.flywheel.web.controller.xianyu.result.RecycleQuoteTemplateResult;
import com.seeease.flywheel.web.entity.XyRecycleIdleTemplate;
import com.seeease.flywheel.web.infrastructure.service.XyRecycleIdleTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Tiro
 * @date 2023/10/25
 */
@Slf4j
@Component
public class RecycleQuoteTemplateRequestProcessor implements BaseQiMenRequestProcessor<RecycleQuoteTemplateRequest, RecycleQuoteTemplateResult> {
    @Resource
    private XyRecycleIdleTemplateService templateService;

    @Override
    public Class<RecycleQuoteTemplateRequest> requestClass() {
        return RecycleQuoteTemplateRequest.class;
    }

    @Override
    public XianYuMethodEnum getMethodEnum() {
        return XianYuMethodEnum.RECYCLE_QUOTE_TEMPLATE;
    }

    @Override
    public RecycleQuoteTemplateResult execute(RecycleQuoteTemplateRequest request) {
        log.info("闲鱼获取回收商的报价模板数据:{}", JSONObject.toJSONString(request));
        XyRecycleIdleTemplate template = templateService.getOneBySpuId(request.getSpuid());

        String temp = "{\"prodName\":\"" + template.getBrandName() + "\",\"spuId\":" + template.getSpuId() + ",\"quoteType\":\"DELAY\",\"sceneType\":\"LUXURIES\",\"questions\":[{\"groupName\":\"\",\"excludeIds\":[],\"groupId\":0,\"answers\":[],\"name\":\"正面整体图\",\"id\":25047969379,\"questionType\":\"PHOTO\",\"required\":true,\"tips\":{\"picUrl\":\"https://gw.alicdn.com/imgextra/i2/O1CN01yA2A2n1p7P8uRBmTf_!!6000000005313-49-tps-1200-1200.webp\",\"tipText\":\"请清晰拍摄手表整体外观细节\"}},{\"groupName\":\"\",\"excludeIds\":[],\"groupId\":0,\"answers\":[],\"name\":\"表盘背面图\",\"id\":25047930718,\"questionType\":\"PHOTO\",\"required\":true,\"tips\":{\"picUrl\":\"https://gw.alicdn.com/imgextra/i1/O1CN012BW8UE1edxWQKJPVk_!!6000000003895-49-tps-1200-1200.webp\",\"tipText\":\"请清晰拍摄表后盖细节图\"}},{\"groupName\":\"\",\"excludeIds\":[],\"groupId\":0,\"answers\":[],\"name\":\"表扣细节图\",\"id\":25047907763,\"questionType\":\"PHOTO\",\"required\":true,\"tips\":{\"picUrl\":\"https://gw.alicdn.com/imgextra/i1/O1CN012roErh1sX07o3XHtM_!!6000000005775-49-tps-1200-1200.webp\",\"tipText\":\"请清晰拍摄表扣细节图\"}},{\"groupName\":\"\",\"excludeIds\":[],\"groupId\":0,\"answers\":[],\"name\":\"表带细节图\",\"id\":25047915921,\"questionType\":\"PHOTO\",\"required\":true,\"tips\":{\"picUrl\":\"https://gw.alicdn.com/imgextra/i2/O1CN01d6brV01taUlOdd8kx_!!6000000005918-49-tps-1200-1200.webp\",\"tipText\":\"请清晰拍摄表带细节图\"}},{\"groupName\":\"\",\"excludeIds\":[],\"groupId\":0,\"answers\":[],\"name\":\"瑕疵图\",\"id\":184530213,\"questionType\":\"PHOTO\",\"required\":false,\"tips\":{\"picUrl\":\"\",\"tipText\":\"请上传使用痕迹的图片\"}},{\"id\":\"311998457\",\"name\":\"表镜尺寸\",\"required\":false,\"questionType\":\"TEXT\",\"answers\":[]},{\"answers\":[{\"id\":3358100,\"name\":\"自动机械\",\"type\":\"TEXT\"},{\"id\":3833203,\"name\":\"手动机械\",\"type\":\"TEXT\"},{\"id\":3311241,\"name\":\"石英\",\"type\":\"TEXT\"}],\"id\":252551185,\"name\":\"机芯类型\",\"questionType\":\"SINGLECHOISE\",\"required\":true},{\"answers\":[{\"id\":3599637,\"name\":\"发票\",\"banned\":false,\"type\":\"TEXT\"},{\"id\":90656377,\"name\":\"身份卡\",\"banned\":false,\"type\":\"TEXT\"},{\"id\":3567650,\"name\":\"包装盒\",\"banned\":false,\"type\":\"TEXT\"}],\"id\":311024544,\"name\":\"配件（可多选）\",\"questionType\":\"MULTICHOICES\",\"required\":false},{\"answers\":[{\"name\":\"全新未使用\",\"id\":496782185,\"banned\":false,\"type\":\"TEXT\"},{\"name\":\"几乎全新\",\"id\":15994218,\"type\":\"TEXT\"},{\"name\":\"轻微使用痕迹\",\"id\":1451890569,\"type\":\"TEXT\"},{\"name\":\"明显使用痕迹\",\"id\":1697085982,\"type\":\"TEXT\"}],\"id\":312202772,\"name\":\"使用情况\",\"questionType\":\"SINGLECHOISE\",\"required\":false}]}";
        return RecycleQuoteTemplateResult.builder()
                .template(temp)
                .build();
    }
}
