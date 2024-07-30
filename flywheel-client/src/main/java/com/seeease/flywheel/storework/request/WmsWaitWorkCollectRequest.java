package com.seeease.flywheel.storework.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/1
 */
@Data
public class WmsWaitWorkCollectRequest implements Serializable {


    /**
     * 作业id集合
     */
    private List<Integer> workIdList;

}
