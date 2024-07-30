package com.seeease.flywheel.web.event;

import com.seeease.flywheel.purchase.request.AutoPurchaseCreateRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Mr. Du
 * @Description 自动创建采购单流程
 * @Date create in 2023/10/10 09:39
 */
@Getter
@Setter
public class AutoPurchaseCreateEvent extends ApplicationEvent {

    private AutoPurchaseCreateRequest autoPurchaseCreateRequest;

    public AutoPurchaseCreateEvent(Object source, AutoPurchaseCreateRequest request) {
        super(source);
        this.autoPurchaseCreateRequest = request;
    }
}
