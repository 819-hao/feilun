package com.seeease.flywheel.web.entity.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderListRequest extends PageRequest implements Serializable {

    /**
     * 状态
     */
    private Integer quoteOrderState;

    /**
     * 搜索关键字
     */
    private String keyword;
}
