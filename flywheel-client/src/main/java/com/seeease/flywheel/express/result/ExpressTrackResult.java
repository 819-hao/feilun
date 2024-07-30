package com.seeease.flywheel.express.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 业务
 * @Date create in 2023/6/27 14:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpressTrackResult implements Serializable {

    private List<ExpressTrackNodeDTO> expressTrackNode;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExpressTrackNodeDTO implements Serializable {

        private String acceptTime;

        private String acceptAddress;

        private String remark;
    }
}
