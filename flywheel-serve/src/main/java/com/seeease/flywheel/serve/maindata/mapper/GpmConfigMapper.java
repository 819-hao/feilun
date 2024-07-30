package com.seeease.flywheel.serve.maindata.mapper;

import com.seeease.flywheel.serve.maindata.entity.GpmConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* @author edy
* @description 针对表【gpm_config(毛利率配置表)】的数据库操作Mapper
* @createDate 2023-03-07 10:27:23
* @Entity com.seeease.flywheel.serve.maindata.entity.GpmConfig
*/
public interface GpmConfigMapper extends BaseMapper<GpmConfig> {
    GpmConfig selectGpmConfigByCreateTime(@Param("batchCreateTime") Date batchCreateTime, @Param("toTarget")String toTarget);
}




