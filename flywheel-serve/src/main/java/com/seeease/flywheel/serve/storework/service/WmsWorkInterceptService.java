package com.seeease.flywheel.serve.storework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.storework.entity.WmsWorkIntercept;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【wms_work_intercept(发货作业拦截表)】的数据库操作Service
 * @createDate 2023-08-31 17:58:28
 */
public interface WmsWorkInterceptService extends IService<WmsWorkIntercept> {

    /**
     * 拦截
     *
     * @param originSerialNoList
     */
    void checkIntercept(List<String> originSerialNoList);
}
