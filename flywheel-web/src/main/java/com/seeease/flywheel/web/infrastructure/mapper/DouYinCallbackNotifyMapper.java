package com.seeease.flywheel.web.infrastructure.mapper;

import com.seeease.flywheel.web.entity.BrandNotify;
import com.seeease.flywheel.web.entity.DouYinCallbackNotify;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【douyin_callback_notify(抖音消息通知)】的数据库操作Mapper
 * @createDate 2023-04-25 16:57:01
 * @Entity com.seeease.flywheel.web.entity.DouyinCallbackNotify
 */
public interface DouYinCallbackNotifyMapper extends SeeeaseMapper<DouYinCallbackNotify> {


    /**
     * 获取品牌到货通知
     *
     * @param stockIdList
     * @return
     */
    List<BrandNotify> getBrandNotify(@Param("stockIdList") List<Integer> stockIdList);
}




