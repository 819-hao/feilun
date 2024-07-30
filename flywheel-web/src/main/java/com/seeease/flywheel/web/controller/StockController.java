package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.IStockLifeCycleFacade;
import com.seeease.flywheel.goods.IStockLogFacade;
import com.seeease.flywheel.goods.entity.StockMarketsInfo;
import com.seeease.flywheel.goods.request.*;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/28 14:39
 */
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade iStockFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStockLifeCycleFacade iStockLifeCycleFacade;


    @DubboReference(check = false, version = "1.0.0")
    private IStockLogFacade stockLogFacade;

    /**
     * 飞轮新的查询商品列表接口
     *
     * @param request
     * @return
     */
    @PostMapping("/selectStockList")
    public SingleResponse selectStockList(@RequestBody StockInfoListRequest request) {
        return SingleResponse.of(iStockFacade.selectStockList(request));
    }

    /**
     * 更加商品编码查库存商品信息
     *
     * @param
     * @return
     */
    @GetMapping("/getByWno")
    public SingleResponse getByWno(@RequestParam("wno") String wno) {
        return SingleResponse.of(iStockFacade.getByWno(wno));
    }

    /**
     * 更加商品编码查库存商品信息
     *
     * @param
     * @return
     */
    @GetMapping("/getByStockSn")
    public SingleResponse getByStockSn(@RequestParam("stockSn") String stockSn) {
        return SingleResponse.of(iStockFacade.getByStockSn(stockSn));
    }

    /**
     * 不登录 根据商品编码查询商品信息
     *
     * @param wno
     * @return
     */
    @GetMapping("/getByWnoNoLogin")
    public SingleResponse getByWnoNoLogin(@RequestParam("wno") String wno) {
        return SingleResponse.of(iStockFacade.getByWnoNoLogin(wno));
    }

    /**
     * 查库存商品信息
     *
     * @param request
     * @return
     */
    @PostMapping("/listStock")
    public SingleResponse listStock(@RequestBody StockListRequest request) {
        return SingleResponse.of(iStockFacade.listStock(request));
    }

    @PostMapping("/lifeCycle")
    public SingleResponse lifeCycle(@RequestBody StockLifecycleListRequest request) {
        request.setPage(1);
        request.setLimit(10000);
        return SingleResponse.of(iStockLifeCycleFacade.list(request));
    }

    /**
     * 异常商品查询
     *
     * @param request
     * @return
     */
    @PostMapping("/exceptionStock")
    public SingleResponse exceptionStock(@RequestBody StockExceptionListRequest request) {

        return SingleResponse.of(iStockLifeCycleFacade.exceptionStock(request));
    }

    /**
     * 更新附件
     *
     * @param request
     * @return
     */
    @PostMapping("/attachment")
    public SingleResponse attachment(@RequestBody StockAttachmentRequest request) {
        return SingleResponse.of(iStockFacade.attachment(request));
    }

    /**
     * 修改表身号
     *
     * @param request
     * @return
     */
    @PostMapping("/updateStockSn")
    public SingleResponse updateStockSn(@RequestBody StockSnUpdateRequest request) {
        iStockFacade.updateStockSn(request);
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/print")
    public SingleResponse print(@RequestBody StockPrintRequest request) {

        return SingleResponse.of(iStockFacade.print(request));
    }

    /**
     * 修改限量序号
     *
     * @param request
     * @return
     */
    @PostMapping("/updateStockLimitedCode")
    public SingleResponse updateStockLimitedCode(@RequestBody UpdateStockLimitedCodeRequest request) {
        iStockFacade.updateStockLimitedCode(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 解除定金锁定
     *
     * @param request
     * @return
     */
    @PostMapping("/unLockDemand")
    public SingleResponse unLockDemand(@RequestBody StockUnLockDemandRequest request) {
        return SingleResponse.of(iStockFacade.unLockDemand(request));
    }

    /**
     * 库存型号列表
     *
     * @return
     */
    @PostMapping("/modelStockFold")
    public SingleResponse modelStockFoldList(@RequestBody StockGoodQueryRequest request) {
        return SingleResponse.of(iStockFacade.modelStockFold(request));
    }

    /**
     * 转入异常商品库
     *
     * @param inExceptionStockRequest
     * @return
     */
    @PostMapping("/inExceptionStock")
    public SingleResponse inExceptionStock(@RequestBody InExceptionStockRequest inExceptionStockRequest) {
        return SingleResponse.of(iStockFacade.inExceptionStock(inExceptionStockRequest.getIds()));
    }

    /**
     * 商品、库存列表共用
     *
     * @return
     */
    @PostMapping("/queryStockPage")
    public SingleResponse queryStockPage(@RequestBody StockGoodQueryRequest request) {
        return SingleResponse.of(iStockFacade.queryStockPage(request));
    }


    @PostMapping("/saveStockMarketsInfo")
    public SingleResponse saveStockMarketsInfo(@RequestBody StockMarketsInfo marketsInfo) {
        iStockFacade.saveStockMarketsInfo(marketsInfo);
        return SingleResponse.buildSuccess();
    }

    /**
     * 修改日志
     *
     * @param request
     * @return
     */
    @PostMapping("/logList")
    public SingleResponse logList(@RequestBody LogStockOptListRequest request) {
        return SingleResponse.of(stockLogFacade.list(request));
    }
}
