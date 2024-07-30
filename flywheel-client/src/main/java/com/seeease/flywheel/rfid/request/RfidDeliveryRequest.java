package com.seeease.flywheel.rfid.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidDeliveryRequest implements Serializable {
    /**
     * 业务参数
     */
    private Object request;

    /**
     * 任务参数
     */
    private List<Object> taskList;


}
