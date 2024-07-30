package com.seeease.flywheel.sf;

import com.seeease.flywheel.sf.request.ExpressOrderCreateRequest;
import com.seeease.flywheel.sf.request.ExpressOrderEditRequest;
import com.seeease.flywheel.sf.request.ExpressOrderQueryRequest;
import com.seeease.flywheel.sf.result.ExpressOrderCreateResult;
import com.seeease.flywheel.sf.result.ExpressOrderEditResult;
import com.seeease.flywheel.sf.result.ExpressOrderQueryResult;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:38
 */

public interface IExpressOrderFacade {

    /**
     * 新建
     *
     * @param request
     * @return
     */
    ExpressOrderCreateResult create(ExpressOrderCreateRequest request);

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    ExpressOrderEditResult edit(ExpressOrderEditRequest request);

    /**
     * 查询
     * @param request
     * @return
     */
    ExpressOrderQueryResult query(ExpressOrderQueryRequest request);
}
