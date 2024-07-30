package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.entity.DataEventTracking;
import com.seeease.flywheel.web.infrastructure.service.DataEventTrackingService;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Tiro
 * @date 2023/9/18
 */
@Slf4j
@RestController
@RequestMapping("/tracking")
public class DataEventTrackingController {
    @Resource
    private DataEventTrackingService dataEventTrackingService;


    @PostMapping("/save")
    public SingleResponse save(@RequestBody JSONObject data) {
        DataEventTracking dataEventTracking = new DataEventTracking();
        dataEventTracking.setTrackingData(data.toJSONString());
        return SingleResponse.of(dataEventTrackingService.save(dataEventTracking));
    }
}
