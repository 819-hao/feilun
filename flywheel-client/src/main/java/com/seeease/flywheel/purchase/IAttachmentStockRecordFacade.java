package com.seeease.flywheel.purchase;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.request.AttachmentStockImportRequest;
import com.seeease.flywheel.purchase.request.AttachmentStockRecordListRequest;
import com.seeease.flywheel.purchase.result.AttachmentStockImportResult;
import com.seeease.flywheel.purchase.result.AttachmentStockRecordListResult;

/**
 * @author Tiro
 * @date 2023/9/25
 */
public interface IAttachmentStockRecordFacade {
    /**
     * 列表
     *
     * @param request
     * @return
     */
    PageResult<AttachmentStockRecordListResult> list(AttachmentStockRecordListRequest request);

    /**
     * 导入创建
     *
     * @param request
     * @return
     */
    AttachmentStockImportResult importHandle(AttachmentStockImportRequest request);
}
