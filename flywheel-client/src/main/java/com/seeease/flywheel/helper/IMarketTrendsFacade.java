package com.seeease.flywheel.helper;


import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.result.MarketTrendsDetailResult;
import com.seeease.flywheel.helper.result.MarketTrendsSearchResult;

/**
 * 行情rpc
 */
public interface IMarketTrendsFacade {
    /**
     * 行情列表页搜索
     * @param page
     * @param limit
     * @param q
     * @param model
     * @return
     */
    PageResult<MarketTrendsSearchResult> search(Integer page, Integer limit, String q, String model);

    /**
     * 图片型号识别
     * @param fileBytes 图片数组
     * @return
     */
    String aiModelMatch(byte[] fileBytes);

    /**
     * 详情
     * @param id
     * @param timeRange
     * @return
     */
    MarketTrendsDetailResult detail(Integer id, Integer timeRange);
}
