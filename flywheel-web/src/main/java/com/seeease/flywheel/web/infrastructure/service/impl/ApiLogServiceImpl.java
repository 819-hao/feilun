package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.ApiLog;
import com.seeease.flywheel.web.infrastructure.mapper.ApiLogMapper;
import com.seeease.flywheel.web.infrastructure.service.ApiLogService;
import org.springframework.stereotype.Service;

/**
* @author dmmasxnmf
* @description 针对表【api_log(api统计)】的数据库操作Service实现
* @createDate 2023-07-26 16:12:00
*/
@Service
public class ApiLogServiceImpl extends ServiceImpl<ApiLogMapper, ApiLog>
    implements ApiLogService {

}




