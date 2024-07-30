package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 维修结果集
 * @Date create in 2023/2/3 11:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixEditResultRequest implements Serializable {

    private Integer fixId;

    private String serialNo;

    /**
     * 维修结果项
     */
    private List<Integer> resultContent;

    /**
     * 维修结果维修单图片(维修结果)
     */
    private List<String> resultImgList;
}
