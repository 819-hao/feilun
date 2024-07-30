package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.AttachmentStockInfoListRequest;
import com.seeease.flywheel.goods.result.AttachmentStockInfo;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStock;
import com.seeease.flywheel.serve.goods.mapper.ExtAttachmentStockMapper;
import com.seeease.flywheel.serve.goods.service.ExtAttachmentStockService;
import org.springframework.stereotype.Service;

/**
 * @author Tiro
 * @description 针对表【ext_attachment_stock(附件库存扩展信息)】的数据库操作Service实现
 * @createDate 2023-09-25 09:54:52
 */
@Service
public class ExtAttachmentStockServiceImpl extends ServiceImpl<ExtAttachmentStockMapper, ExtAttachmentStock>
        implements ExtAttachmentStockService {

    @Override
    public PageResult<AttachmentStockInfo> listStock(AttachmentStockInfoListRequest request) {
        Page<AttachmentStockInfo> page = baseMapper.listStock(Page.of(request.getPage(), request.getLimit()), request);
        return PageResult.<AttachmentStockInfo>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }
}




