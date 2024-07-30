package com.seeease.flywheel.k3cloud;

import com.seeease.flywheel.k3cloud.request.K3cloudGlVoucherRequest;
import com.seeease.flywheel.k3cloud.result.K3cloudGlVoucherResult;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/2 17:54
 */

public interface IK3cloudGlVoucherFacade {

    /**
     * 本接口用于实现凭证 (GL_VOUCHER) 的单据查询(ExecuteBillQuery)功能
     *
     * @param request
     * @return
     */
    K3cloudGlVoucherResult executeBillQuery(K3cloudGlVoucherRequest request);
}
