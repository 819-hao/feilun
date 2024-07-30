package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IGoodsExtFacade;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @DubboReference(check = false, version = "1.0.0")
    private IGoodsExtFacade goodsExtFacade;

    /**
     * 查商品型号信息
     *
     * @param request
     * @return
     */
    @PostMapping("/listGoods")
    public SingleResponse listGoods(@RequestBody GoodsListRequest request) {
        return SingleResponse.of(goodsExtFacade.listGoods(request));
    }


}
