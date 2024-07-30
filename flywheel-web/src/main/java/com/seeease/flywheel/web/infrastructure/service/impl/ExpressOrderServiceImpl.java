package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.ExpressOrder;
import com.seeease.flywheel.web.entity.enums.ExpressOrderStateEnum;
import com.seeease.flywheel.web.infrastructure.mapper.ExpressOrderMapper;
import com.seeease.flywheel.web.infrastructure.service.ExpressOrderService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【express_order(物流单)】的数据库操作Service实现
 * @createDate 2023-09-19 16:00:02
 */
@Service
public class ExpressOrderServiceImpl extends ServiceImpl<ExpressOrderMapper, ExpressOrder>
        implements ExpressOrderService {

    @Override
    public int insertBatchSomeColumn(List<ExpressOrder> expressOrderList) {
        return baseMapper.insertBatchSomeColumn(expressOrderList);
    }

    @Override
    public void upAndStateTransition(ExpressOrder order, ExpressOrderStateEnum.TransitionEnum transitionEnum) {
        order.setTransitionStateEnum(transitionEnum);
        UpdateByIdCheckState.update(baseMapper, order);
    }

}




