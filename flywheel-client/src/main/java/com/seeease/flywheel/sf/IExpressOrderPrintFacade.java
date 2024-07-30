package com.seeease.flywheel.sf;

import com.seeease.flywheel.sf.request.ExpressOrderPrintCreateRequest;
import com.seeease.flywheel.sf.result.ExpressOrderPrintCreateResult;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:38
 */

public interface IExpressOrderPrintFacade {

    /**
     * 新建
     *
     * @param request
     * @return
     */
    ExpressOrderPrintCreateResult create(ExpressOrderPrintCreateRequest request);
}
