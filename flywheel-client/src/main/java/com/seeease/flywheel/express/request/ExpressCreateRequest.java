package com.seeease.flywheel.express.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 创建顺丰单接口 业务
 * @Date create in 2023/6/25 13:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressCreateRequest implements Serializable {

    private Integer id;

    private String serialNo;

    private List<ExpressDetailDto> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpressDetailDto implements Serializable {

        /**
         * 商品id
         */
        private Integer stockId;

        /**
         * 行id
         */
        private Integer id;

        /**
         * 出库单号
         */
        private String serialNo;

    }
}
