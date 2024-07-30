package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.purchase.entity.AttachmentStockImportDto;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStockRecord;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【ext_attachment_stock_record(附件库存导入记录)】的数据库操作Service
 * @createDate 2023-09-25 09:54:52
 */
public interface ExtAttachmentStockRecordService extends IService<ExtAttachmentStockRecord> {

    /**
     * 导入创建
     *
     * @param dtoList
     * @return
     */
    List<ExtAttachmentStockRecord> importCreate(List<AttachmentStockImportDto> dtoList);
}
