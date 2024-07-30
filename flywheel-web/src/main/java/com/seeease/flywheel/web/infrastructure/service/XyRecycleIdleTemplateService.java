package com.seeease.flywheel.web.infrastructure.service;

import com.seeease.flywheel.web.entity.XyRecycleIdleTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Tiro
* @description 针对表【xy_recycle_idle_template(闲鱼估价问卷模版)】的数据库操作Service
* @createDate 2023-10-20 11:17:58
*/
public interface XyRecycleIdleTemplateService extends IService<XyRecycleIdleTemplate> {

    XyRecycleIdleTemplate getOneBySpuId(String spuId);
}
