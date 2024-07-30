package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.maindata.enums.FixSiteEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 维修站点和站点等级
 *
 * @TableName fix_site
 */
@TableName(value = "fix_site")
@Data
public class FixSite extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

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
    private FixSiteEnum siteState;

    private Integer parentFixSiteId;

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


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}