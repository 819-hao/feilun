package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.AllocateDetailsRequest;
import com.seeease.flywheel.allocate.request.AllocateExportListRequest;
import com.seeease.flywheel.allocate.request.AllocateListRequest;
import com.seeease.flywheel.allocate.result.AllocateDetailsResult;
import com.seeease.flywheel.allocate.result.AllocateExportListResult;
import com.seeease.flywheel.allocate.result.AllocateListResult;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2023/3/8
 */
@Slf4j
@RestController
@RequestMapping("/allocate")
public class AllocateController {
    @DubboReference(check = false, version = "1.0.0")
    private IAllocateFacade allocateFacade;

    /**
     * 调拨列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody AllocateListRequest request) {
        PageResult<AllocateListResult> result = allocateFacade.list(request);
        return SingleResponse.of(result);
    }

    /**
     * 调拨详情
     *
     * @param request
     * @return
     */
    @PostMapping("/details")
    public SingleResponse details(@RequestBody AllocateDetailsRequest request) {
        AllocateDetailsResult result = allocateFacade.details(request);
        return SingleResponse.of(result);
    }

    /**
     * 调拨导出
     * @param request
     * @return
     */
    @PostMapping("/export")
    public SingleResponse export(@RequestBody AllocateExportListRequest request) {
        PageResult<AllocateExportListResult> result = allocateFacade.export(request);
        return SingleResponse.of(result);
    }
}
