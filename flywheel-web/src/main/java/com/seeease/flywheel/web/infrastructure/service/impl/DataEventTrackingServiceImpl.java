package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.DataEventTracking;
import com.seeease.flywheel.web.infrastructure.service.DataEventTrackingService;
import com.seeease.flywheel.web.infrastructure.mapper.DataEventTrackingMapper;
import org.springframework.stereotype.Service;

/**
* @author Tiro
* @description 针对表【data_event_tracking(数据埋点)】的数据库操作Service实现
* @createDate 2023-09-18 14:42:12
*/
@Service
public class DataEventTrackingServiceImpl extends ServiceImpl<DataEventTrackingMapper, DataEventTracking>
    implements DataEventTrackingService{

}




