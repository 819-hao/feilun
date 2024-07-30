package com.seeease.flywheel.rfid;

import com.seeease.flywheel.rfid.result.RfidConfigResult;
import com.seeease.flywheel.rfid.result.RfidOutStoreListResult;
import com.seeease.flywheel.rfid.result.RfidShopReceiveListResult;


import java.util.List;

public interface IRfidFacade {
    /**
     * 获取rfid配置
     * @param platform
     * @return
     */
    RfidConfigResult config(Integer platform);

    /**
     * rfid查找出库列表
     * @param shopId 门店id
     * @param q 查询条件
     * @return
     */
    List<RfidOutStoreListResult> rfidWaitOutStoreList(Integer shopId, String q,List<String> brandNameList);

    /**
     * rif查找门店待入库列表
     * @param q
     * @return
     */
    List<RfidShopReceiveListResult> rfidWaitReceiveList(String q);

    /**
     * 根据门店id查找仓库id
     * @param shopId
     * @return
     */
    Integer queryStoreIdByShopId(Integer shopId);
}
