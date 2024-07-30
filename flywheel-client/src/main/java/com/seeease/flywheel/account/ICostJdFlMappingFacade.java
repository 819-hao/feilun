package com.seeease.flywheel.account;

import com.seeease.flywheel.account.result.CostJdFlMappingResult;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/23 14:47
 */

public interface ICostJdFlMappingFacade {

    /**
     * 全量匹配
     *
     * @return
     */
    List<CostJdFlMappingResult> list();
}
