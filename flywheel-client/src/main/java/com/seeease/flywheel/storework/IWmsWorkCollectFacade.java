package com.seeease.flywheel.storework;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.storework.request.WmsWaitWorkCollectRequest;
import com.seeease.flywheel.storework.request.WmsWorkListRequest;
import com.seeease.flywheel.storework.request.WmsWorkUploadExpressRequest;
import com.seeease.flywheel.storework.result.*;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/8/31
 */
public interface IWmsWorkCollectFacade {
    /**
     * 集单
     *
     * @param request
     * @return
     */
    WmsWaitWorkCollectResult collectWork(WmsWaitWorkCollectRequest request);

    /**
     * 工作列表
     *
     * @param request
     * @return
     */
    PageResult<WmsWorkListResult> listWork(WmsWorkListRequest request);

    /**
     * 聚合统计数量
     *
     * @param request
     * @return
     */
    List<WmsWorkCollectCountResult> count(WmsWorkListRequest request);

    /**
     * 快递打单详情
     *
     * @param originSerialNo
     * @return
     */
    WmsWorkExpressResult express(String originSerialNo);

    /**
     * 打单上传快递单号
     *
     * @param request
     * @return
     */
    WmsWorkUploadExpressResult uploadExpress(WmsWorkUploadExpressRequest request);
}
