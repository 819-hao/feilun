package com.seeease.flywheel.web.event;

import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 15:21
 */
@Getter
@Setter
public class PricingStartEvent extends ApplicationEvent {

    private List<PricingCreateRequest> pricingCreateRequestList;

    public PricingStartEvent(Object source, List<PricingCreateRequest> pricingCreateRequestList) {
        super(source);
        this.pricingCreateRequestList = pricingCreateRequestList;
    }
}
