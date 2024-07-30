package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.anomaly.IAnomalyFacade;
import com.seeease.flywheel.anomaly.request.AnomalyListRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/4/13 11:31
 */
@Slf4j
@RestController
@RequestMapping("/anomaly")
public class AnomalyController {

    @DubboReference(check = false, version = "1.0.0")
    private IAnomalyFacade anomalyFacade;

    @PostMapping("/list")
    public SingleResponse list(@RequestBody AnomalyListRequest request) {
        return SingleResponse.of(anomalyFacade.list(request));
    }
}
