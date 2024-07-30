package com.seeease.flywheel.qt.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 质检收货
 * @Date create in 2023/1/17 14:06
 */
@Data
public class QualityTestingReceiveRequest implements Serializable {

    private List<QualityTestingReceiveListRequest> list;

    @Data
    public class QualityTestingReceiveListRequest implements Serializable {
        private Integer qualityTestingId;
    }
}
