package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.request.AttachmentStockInfoListRequest;
import com.seeease.flywheel.goods.result.AttachmentStockInfo;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStock;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Tiro
 * @description 针对表【ext_attachment_stock(附件库存扩展信息)】的数据库操作Mapper
 * @createDate 2023-09-25 09:54:52
 * @Entity com.seeease.flywheel.serve.goods.entity.ExtAttachmentStock
 */
public interface ExtAttachmentStockMapper extends SeeeaseMapper<ExtAttachmentStock> {

    /**
     * @param page
     * @param request
     * @return
     */
    Page<AttachmentStockInfo> listStock(Page page, @Param("request") AttachmentStockInfoListRequest request);
}




