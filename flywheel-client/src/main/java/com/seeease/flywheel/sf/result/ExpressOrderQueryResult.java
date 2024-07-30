package com.seeease.flywheel.sf.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressOrderQueryResult implements Serializable {

    private List<ExpressOrderQueryDTO> list;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ExpressOrderQueryDTO implements Serializable {

        private Integer id;

        private String serialNo;

        private String expressNo;

        private String sonSerialNo;

        private Integer expressState;

        private Integer expressChannel;

        private Long douYinShopId;
    }
}
