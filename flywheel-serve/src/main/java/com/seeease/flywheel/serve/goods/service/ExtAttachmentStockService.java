package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.AttachmentStockInfoListRequest;
import com.seeease.flywheel.goods.result.AttachmentStockInfo;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStock;

/**
 * @author Tiro
 * @description 针对表【ext_attachment_stock(附件库存扩展信息)】的数据库操作Service
 * @createDate 2023-09-25 09:54:52
 */
public interface ExtAttachmentStockService extends IService<ExtAttachmentStock> {

    /**
     * @param request
     * @return
     */
    PageResult<AttachmentStockInfo> listStock(AttachmentStockInfoListRequest request);
}
