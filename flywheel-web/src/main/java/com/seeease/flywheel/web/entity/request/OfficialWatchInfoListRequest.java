package com.seeease.flywheel.web.entity.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @author Tiro
 * @date 2023/6/30
 */
@Data
public class OfficialWatchInfoListRequest extends PageRequest {


    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;
}
