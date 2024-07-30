package com.seeease.flywheel.fix.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 维修收货
 * @Date create in 2023/1/17 17:25
 */
@Data
public class FixReceiveRequest implements Serializable {

    private List<FixReceiveListRequest> list;

    @Data
    public static class FixReceiveListRequest implements Serializable {

        private Integer fixId;

    }
}
