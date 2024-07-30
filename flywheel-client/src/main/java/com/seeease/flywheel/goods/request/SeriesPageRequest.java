package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesPageRequest extends PageRequest {


    /**
     * 品牌
     */
    private String brandName;
    private Integer brandId;
    /**
     * 系列
     */
    private String name;

    /**
     * vulgo
     */
    private String vulgo;

    private Integer type;
}
