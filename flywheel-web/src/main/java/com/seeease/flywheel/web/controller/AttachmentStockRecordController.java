package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.purchase.IAttachmentStockRecordFacade;
import com.seeease.flywheel.purchase.request.AttachmentStockRecordListRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Slf4j
@RestController
@RequestMapping("/attachmentStockRecord")
public class AttachmentStockRecordController {

    @DubboReference(check = false, version = "1.0.0")
    private IAttachmentStockRecordFacade attachmentStockRecordFacade;

    /**
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody AttachmentStockRecordListRequest request) {
        return SingleResponse.of(attachmentStockRecordFacade.list(request));
    }
}
