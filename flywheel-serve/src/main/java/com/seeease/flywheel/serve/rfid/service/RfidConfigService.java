package com.seeease.flywheel.serve.rfid.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.rfid.result.RfidConfigResult;
import com.seeease.flywheel.serve.rfid.entity.RfidConfig;

public interface RfidConfigService extends IService<RfidConfig> {
     RfidConfigResult config(Integer platform);
}
