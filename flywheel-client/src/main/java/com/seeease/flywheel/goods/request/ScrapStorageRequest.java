package com.seeease.flywheel.goods.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class ScrapStorageRequest implements Serializable {

    private String scrapReason;

    private String batchImagUrl;

    /**
     *
     */
    private List<LineDto> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineDto implements Serializable {

        /**
         * 库存id
         */
        private Integer stockId;

    }

}
