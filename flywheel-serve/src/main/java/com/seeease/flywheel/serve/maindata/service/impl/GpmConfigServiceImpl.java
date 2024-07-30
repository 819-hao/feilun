package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.GpmConfig;
import com.seeease.flywheel.serve.maindata.service.GpmConfigService;
import com.seeease.flywheel.serve.maindata.mapper.GpmConfigMapper;
import org.springframework.stereotype.Service;

/**
* @author edy
* @description 针对表【gpm_config(毛利率配置表)】的数据库操作Service实现
* @createDate 2023-03-07 10:27:23
*/
@Service
public class GpmConfigServiceImpl extends ServiceImpl<GpmConfigMapper, GpmConfig>
    implements GpmConfigService{

}




