package com.seeease.flywheel.serve.fix.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.fix.request.*;
import com.seeease.flywheel.fix.result.*;
import com.seeease.flywheel.serve.fix.entity.BillFix;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_fix(维修记录)】的数据库操作Service
 * @createDate 2023-01-17 11:25:35
 */
public interface BillFixService extends IService<BillFix> {


    /**
     * 创建
     *
     * @param request
     * @return
     */
    FixCreateResult create(FixCreateRequest request);

    /**
     * 收货
     *
     * @param request
     * @return
     */
    List<FixReceiveListResult> receive(List<FixReceiveRequest.FixReceiveListRequest> request);

    /**
     * 接修
     *
     * @param request
     * @return
     */
    FixRepairResult repair(FixRepairRequest request);

    /**
     * 维修完成
     *
     * @param request
     * @return
     */
    FixFinishResult finish(FixFinishRequest request);

    /**
     * 质检维修
     *
     * @param request
     * @return
     */
    QtFixResult qt(QtFixRequest request);

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    Page<FixListResult> page(FixListRequest request);

    /**
     * 维修更新
     *
     * @param request
     * @return
     */
    FixEditResult edit(FixEditRequest request);

    /**
     * 是否加急
     *
     * @param request
     */
    void edit(FixSpecialExpeditingRequest request);

    /**
     * 分配
     *
     * @param request
     * @return
     */
    FixAllotResult allot(FixAllotRequest request);

    /**
     * 送外
     * @param request
     * @return
     */
    FixForeignResult foreign(FixForeignRequest request);
}
