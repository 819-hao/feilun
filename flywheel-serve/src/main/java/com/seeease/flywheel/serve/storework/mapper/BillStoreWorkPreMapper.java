package com.seeease.flywheel.serve.storework.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.result.B3SaleReturnOrderListResult;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.rfid.result.RfidShopReceiveListResult;
import com.seeease.flywheel.rfid.result.RfidOutStoreListResult;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_store_work_pre(总部入库作业单)】的数据库操作Mapper
 * @createDate 2023-01-17 11:16:02
 * @Entity com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre
 */
public interface BillStoreWorkPreMapper extends SeeeaseMapper<BillStoreWorkPre> {

    /**
     * rfid查找待出库列表
     * @param shopId 门店id
     * @param q 查询条件
     * @param brandNameList 品牌名字
     * @return
     */
    List<RfidOutStoreListResult> rfidWaitOutStoreList(@Param("sid") Integer shopId,
                                                      @Param("q") String q,
                                                      @Param("gid") Integer goodsId,
                                                      @Param("waitStatus") Integer waitStatus,
                                                      @Param("brandNameList") List<String> brandNameList);

    /**
     * rfid查找待入库列表
     * @param shopId
     * @param q
     * @param goodsId
     * @return
     */
    List<RfidShopReceiveListResult> rfidWaitReceiveList(@Param("sid") Integer shopId,
                                                        @Param("q") String q,
                                                        @Param("gid") Integer goodsId);

    /**
     * 3号楼退货
     * @param page
     * @param lsShopIds
     * @param b3ShopId
     * @param request
     * @return
     */
    Page<B3SaleReturnOrderListResult> b3Page(Page<Object> page,
                                             @Param("b3ShopId") List<Integer> b3ShopId,@Param("req") B3SaleReturnOrderListRequest request);
}




