package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.ExpressOrderPrint;
import com.seeease.flywheel.web.infrastructure.service.ExpressOrderPrintService;
import com.seeease.flywheel.web.infrastructure.mapper.ExpressOrderPrintMapper;
import org.springframework.stereotype.Service;

/**
* @author Tiro
* @description 针对表【express_order_print(物流单打印)】的数据库操作Service实现
* @createDate 2023-09-19 16:00:02
*/
@Service
public class ExpressOrderPrintServiceImpl extends ServiceImpl<ExpressOrderPrintMapper, ExpressOrderPrint>
    implements ExpressOrderPrintService{

}




