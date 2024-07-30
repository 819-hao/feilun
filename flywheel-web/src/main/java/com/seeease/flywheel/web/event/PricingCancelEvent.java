package com.seeease.flywheel.web.event;

import com.seeease.flywheel.pricing.request.PricingCancelRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description 定价取消事件
 * @Date create in 2023/3/30 10:04
 */
@Getter
@Setter
public class PricingCancelEvent extends ApplicationEvent {

    private List<PricingCancelRequest> pricingCancelRequestList;

    public PricingCancelEvent(Object source, List<PricingCancelRequest> pricingCancelRequestList) {
        super(source);
        this.pricingCancelRequestList = pricingCancelRequestList;
    }
}
