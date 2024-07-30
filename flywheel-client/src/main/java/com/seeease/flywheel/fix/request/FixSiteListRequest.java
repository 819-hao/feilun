package com.seeease.flywheel.fix.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修站点编辑相关对象
 * @Date create in 2023/11/18 10:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixSiteListRequest extends PageRequest implements Serializable {

    /**
     * 站点状态
     */
    private Integer siteState;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 站点名称
     */
    private String siteName;

    //特殊查询 一对多查询

    private Integer parentFixSiteId;

    private String parentFixSiteSerialNo;

    private Integer tagType;

}
