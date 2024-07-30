package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.XyRecycleIdleTemplate;
import com.seeease.flywheel.web.infrastructure.mapper.XyRecycleIdleTemplateMapper;
import com.seeease.flywheel.web.infrastructure.service.XyRecycleIdleTemplateService;
import org.springframework.stereotype.Service;

/**
 * @author Tiro
 * @description 针对表【xy_recycle_idle_template(闲鱼估价问卷模版)】的数据库操作Service实现
 * @createDate 2023-10-20 11:17:58
 */
@Service
public class XyRecycleIdleTemplateServiceImpl extends ServiceImpl<XyRecycleIdleTemplateMapper, XyRecycleIdleTemplate>
        implements XyRecycleIdleTemplateService {


    @Override
    public XyRecycleIdleTemplate getOneBySpuId(String spuId) {
        return baseMapper.selectList(Wrappers.<XyRecycleIdleTemplate>lambdaQuery()
                        .eq(XyRecycleIdleTemplate::getSpuId, spuId))
                .stream().findFirst()
                .orElse(null);
    }

}




