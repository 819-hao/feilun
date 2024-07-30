package com.seeease.flywheel.serve.storework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.storework.entity.WmsWorkIntercept;
import com.seeease.flywheel.serve.storework.mapper.WmsWorkInterceptMapper;
import com.seeease.flywheel.serve.storework.service.WmsWorkInterceptService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【wms_work_intercept(发货作业拦截表)】的数据库操作Service实现
 * @createDate 2023-08-31 17:58:28
 */
@Service
public class WmsWorkInterceptServiceImpl extends ServiceImpl<WmsWorkInterceptMapper, WmsWorkIntercept>
        implements WmsWorkInterceptService {

    @Override
    public void checkIntercept(List<String> originSerialNoList) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<WmsWorkIntercept>()
                .in(WmsWorkIntercept::getOriginSerialNo, originSerialNoList)
                .eq(WmsWorkIntercept::getInterceptState, WhetherEnum.YES.getValue())) > 0) {
            throw new OperationRejectedException(OperationExceptionCode.DOU_YIN_ORDER_EXIST_REFUND);
        }
    }


}




