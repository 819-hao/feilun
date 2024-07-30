package com.seeease.flywheel.goods;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.AttachmentStockInfoListRequest;
import com.seeease.flywheel.goods.result.AttachmentStockInfo;

/**
 * @author Tiro
 * @date 2024/1/19
 */
public interface IAttachmentStockFacade {
    /**
     * @param request
     * @return
     */
    PageResult<AttachmentStockInfo> pageList(AttachmentStockInfoListRequest request);
}
