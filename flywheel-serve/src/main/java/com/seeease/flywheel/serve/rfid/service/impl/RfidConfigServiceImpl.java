package com.seeease.flywheel.serve.rfid.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.rfid.result.RfidConfigResult;


import com.seeease.flywheel.serve.rfid.convert.RfidConfigConverter;
import com.seeease.flywheel.serve.rfid.entity.RfidConfig;
import com.seeease.flywheel.serve.rfid.mapper.RfidConfigMapper;
import com.seeease.flywheel.serve.rfid.service.RfidConfigService;
import org.springframework.stereotype.Service;

@Service
public class RfidConfigServiceImpl extends ServiceImpl<RfidConfigMapper, RfidConfig> implements RfidConfigService {
    @Override
    public RfidConfigResult config(Integer platform) {

        LambdaQueryWrapper<RfidConfig> qw = Wrappers.<RfidConfig>lambdaQuery().eq(RfidConfig::getPlatform, platform).orderByDesc(RfidConfig::getId).last("limit 1");
        return RfidConfigConverter.INSTANCE.convert(baseMapper.selectOne(qw));
    }
}
