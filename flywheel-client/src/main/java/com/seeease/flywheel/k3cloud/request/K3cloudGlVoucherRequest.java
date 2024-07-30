package com.seeease.flywheel.k3cloud.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/7 11:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class K3cloudGlVoucherRequest implements Serializable {

    /**
     * 需求内码
     */
    private List<String> codeList;

    /**
     * 开始时间
     */
    private String completeDateStart;

    /**
     * 结束时间
     */
    private String completeDateEnd;

    /**
     * 是否定时任务
     */
    private boolean isTask;
}
