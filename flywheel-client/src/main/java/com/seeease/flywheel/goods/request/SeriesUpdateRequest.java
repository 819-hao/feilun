package com.seeease.flywheel.goods.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesUpdateRequest implements Serializable {

    private Integer id;

    /**
     * 系列名称
     */
    private String name;

    /**
     * 品牌id
     */
    private Integer brandId;
    /**
     * 类型
     */
    private Integer type;

    /**
     * 俗称
     */
    private String vulgo;
}
