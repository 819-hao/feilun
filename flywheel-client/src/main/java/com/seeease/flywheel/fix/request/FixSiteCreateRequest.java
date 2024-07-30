package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修站点创建相关对象
 * @Date create in 2023/11/18 10:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixSiteCreateRequest implements Serializable {

    /**
     * 0 维修站点等级 1 维修站点
     */
    private Integer tagType;

    /**
     * 0 公司内部站点 1 公司外部站点
     */
    private Integer siteType;

    /**
     * 站点编号
     */
    private String serialNo;

    /**
     * 关联门店id
     */
    private Integer originStoreId;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 站点状态
     */
    private Integer siteState;

    /**
     * 站点联系电话
     */
    private String sitePhone;

    /**
     * 站点联系地址
     */
    private String siteAddress;

    /**
     * 备注
     */
    private String remarks;

    private Integer parentFixSiteId;

    private String parentFixSiteSerialNo;
}
