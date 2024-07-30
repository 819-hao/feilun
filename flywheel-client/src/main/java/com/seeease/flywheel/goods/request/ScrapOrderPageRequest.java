package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapOrderPageRequest extends PageRequest {

    private String serialNo;

    private String stockSn;

    private String startCreatedTime;

    private String endCreatedTime;
}
