package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IAttachmentStockFacade;
import com.seeease.flywheel.goods.request.AttachmentStockInfoListRequest;
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
@RequestMapping("/attachmentStock")
public class AttachmentStockController {

    @DubboReference(check = false, version = "1.0.0")
    private IAttachmentStockFacade attachmentStockFacade;

    /**
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody AttachmentStockInfoListRequest request) {
        return SingleResponse.of(attachmentStockFacade.pageList(request));
    }
}
