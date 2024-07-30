package com.seeease.flywheel.fix.request;

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
public class FixSiteEditRequest implements Serializable {

    private Integer id;

    /**
     * 站点编号
     */
    private String serialNo;

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
}
